package org.pixielib.content;

import org.apache.commons.codec.digest.DigestUtils;
import org.pixielib.util.Hash;
import org.pixielib.util.RandomPerm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class EventStore {
    private static final int DEFAULT_ENTRIES = 10000;

    private BlockIO io;
    private int tablesize;
    private int pages;
    private BucketPage page;
    private RandomPerm perm;
    private Repository repo;
    private int fillcount;

    public EventStore() {
        io = new BlockIO();
        page = new BucketPage();
        perm = new RandomPerm();
        repo = new Repository();
        fillcount = 0;
    }

    public void close() throws IOException {
        io.close();
        repo.close();
    }

    public void open() throws IOException {
        close();
        File file = File.createTempFile("store-", ".idx");
        file.deleteOnExit();
        mktable(file);
        repo.open();
    }

    public void open(String filename) throws IOException {
        close();
        mktable(new File(filename));
    }

    private void mktable(File file) throws IOException {
        tablesize = (int) Primes.prime(DEFAULT_ENTRIES);
        perm.generate(tablesize);
        pages = (tablesize / BucketPage.BUCKETS_PER_PAGE) + 1;
        io.open(file, "rw");
        writeBlock(pages - 1);
    }

    private void writeBlock(long blockno) throws IOException {
        page.rewind();
        io.writeBlock(blockno, page);
    }

    private void readBlock(long blockno) throws IOException {
        page.rewind();
        io.readBlock(blockno, page);
    }

    public boolean insert(Event event) throws IOException {
        String key = event.getObjectId();
        Slot slot = new Slot();
        if (!findSlot(key, slot))
            return false;

        setKey(slot.bucket, key);

        long offset = repo.writeEvent(event);

        page.setFilled(slot.bucket);
        page.setDatumOffset(slot.bucket, offset);

        fillcount++;
        writeBlock(slot.pageno);

        if (isfull()) {
            resize();
        }

        return true;
    }

    private void resize() {
    }

    private boolean isfull() {
        return false;
    }

    private byte[] sha1(String s) {
        return DigestUtils.sha1(s);
    }

    private void setKey(int bucket, String key) {
        page.setDigest(bucket, sha1(key));
    }

    private boolean findSlot(String key, Slot slot) throws IOException {
        return findSlot(sha1(key), slot);
    }

    private boolean findSlot(byte[] digest, Slot slot) throws IOException {
        long h = hash(digest);
        slot.pageno = h / BucketPage.BUCKETS_PER_PAGE;
        slot.bucket = (int) (h % BucketPage.BUCKETS_PER_PAGE);

        readBlock(slot.pageno);

        if (page.isEmpty(slot.bucket))
            return true;

        final byte[] bdigest = new byte[BucketPage.SHA1_DIGEST_BYTES];
        for (int i = 0; i < tablesize; ++i) {
            if (page.isEmpty(slot.bucket))
                return true;

            page.getDigest(slot.bucket, bdigest);
            if (isEqualDigest(bdigest, digest))
                return false;   // already exists

            nextBucket(i, slot);
        }

        return false;
    }

    private long hash(String key) {
        return hash(sha1(key));
    }

    private long hash(byte[] digest) {
        return (Hash.hash(digest) & 0x7FFFFFFFFFFFFFFFL) % tablesize;
    }

    private void nextBucket(int i, Slot slot) throws IOException {
        long realbucket = BucketPage.BUCKETS_PER_PAGE * slot.pageno + slot.bucket;
        long nextbucket = (realbucket + perm(i)) % tablesize;
        long nextpage = nextbucket / BucketPage.BUCKETS_PER_PAGE;
        slot.bucket = (int) (nextbucket % BucketPage.BUCKETS_PER_PAGE);
        if (slot.pageno != nextpage) {
            readBlock(nextpage);
            slot.pageno = nextpage;
        }
    }

    private long perm(int i) {
        return 1 + perm.get(i);    // pseudo-random probing
    }

    private boolean isEqualDigest(byte[] a, byte[] b) {
        return Arrays.equals(a, b);
    }

    public boolean find(Event event) throws IOException {
        return find(event.getObjectId());
    }

    public boolean find(String key) throws IOException {

        Slot slot = new Slot();
        if (!getBucket(key, slot))
            return false;

        return !page.isDeleted(slot.bucket);
    }

    public boolean find(String key, Event event) throws IOException {
        EventBuffer buffer = new EventBuffer();

        if (!find(key, buffer))
            return false;

        event.set(buffer);

        return true;
    }

    public boolean find(String key, EventBuffer buffer) throws IOException {

        Slot slot = new Slot();
        if (!getBucket(key, slot))
            return false;

        if (page.isDeleted(slot.bucket))
            return false;

        long offset = page.getDatumOffset(slot.bucket);

        repo.readEvent(offset, buffer);

        return true;
    }

    private boolean getBucket(String key, Slot slot) throws IOException {
        long h = hash(key);
        slot.pageno = h / BucketPage.BUCKETS_PER_PAGE;
        slot.bucket = (int) (h % BucketPage.BUCKETS_PER_PAGE);

        readBlock(slot.pageno);
        if (page.isEmpty(slot.bucket))
            return false;   // no hit

        final byte[] bdigest = new byte[BucketPage.SHA1_DIGEST_BYTES];
        final byte[] kdigest = sha1(key);
        for (int i = 0; i < tablesize; ++i) {
            if (page.isEmpty(slot.bucket))
                return false;   // no hit

            page.getDigest(slot.bucket, bdigest);
            if (isEqualDigest(bdigest, kdigest))
                return true;    // hit

            nextBucket(i, slot);
        }

        return false;
    }

    public void update(Event event) {
    }
}
