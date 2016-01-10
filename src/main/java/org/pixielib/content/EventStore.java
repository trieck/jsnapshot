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
    private long tablesize;
    private long nbpages;
    private BucketPage page;
    private RandomPerm perm;

    public EventStore() {
        io = new BlockIO();
        page = new BucketPage();
        perm = new RandomPerm();
    }

    public void close() throws IOException {
        io.close();
    }

    public void open() throws IOException {
        close();
        File file = File.createTempFile("store-", ".idx");
        file.deleteOnExit();
        mktable(file);
    }

    public void open(String filename) throws IOException {
        close();
        mktable(new File(filename));
    }

    private void mktable(File file) throws IOException {
        tablesize = Primes.prime(DEFAULT_ENTRIES);
        perm.generate((int) tablesize);
        nbpages = (tablesize / BucketPage.BUCKETS_PER_PAGE) + 1;
        io.open(file, "rw");
        writeBlock(nbpages - 1);
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
        setFilled(slot.bucket);

        writeBlock(slot.pageno);

        return true;
    }

    private void setFilled(int bucket) {
        page.setFilled(bucket);
    }

    private void setKey(int bucket, String key) {
        byte[] bytes = DigestUtils.sha1(key);
        page.setKey(bucket, bytes);
    }

    private boolean findSlot(String key, Slot slot) throws IOException {
        return findSlot(DigestUtils.sha1(key), slot);
    }

    private boolean findSlot(byte[] digest, Slot slot) throws IOException {
        long h = hash(digest);
        slot.pageno = h / BucketPage.BUCKETS_PER_PAGE;
        slot.bucket = (int) (h % BucketPage.BUCKETS_PER_PAGE);

        readBlock(slot.pageno);

        if (!page.isFilled(slot.bucket))
            return true;

        final byte[] bdigest = new byte[BucketPage.SHA1_DIGEST_BYTES];
        for (int i = 0; i < tablesize; ++i) {
            if (!page.isFilled(slot.bucket))
                return true;

            page.getDigest(slot.bucket, bdigest);
            if (isEqualDigest(bdigest, digest))
                return false;   // already exists

            nextBucket(i, slot);
        }

        return false;
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
}
