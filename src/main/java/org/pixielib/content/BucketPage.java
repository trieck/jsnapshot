package org.pixielib.content;

import java.nio.ByteBuffer;

public class BucketPage extends Block {

    /**
     * Each bucket page stores up to 128 32-byte buckets.
     * <p>
     * The structure of a bucket is defined by:
     * <p>
     * FLAGS        : bucket flags (4-bytes)
     * OFFSET       : offset to datum (8-bytes)
     * DIGEST       : sha-1 digest of key (20-bytes)
     */
    public static final int SHA1_DIGEST_BYTES = 20;
    private static final int BF_FILLED = 1;
    private static final int BF_DELETED = 1 << 1;
    private static final int BUCKET_SIZE = 32;
    public static final int BUCKETS_PER_PAGE = Block.BLOCK_SIZE / BUCKET_SIZE;
    private static final int DIGEST_OFFSET = 12;
    private static final int DATUM_OFFSET = 4;

    public void setDigest(int bucket, byte[] bytes) {
        ByteBuffer buffer = getBuffer();

        int offset = bucket * BUCKET_SIZE + DIGEST_OFFSET;
        for (int i = 0; i < SHA1_DIGEST_BYTES; ++i) {
            buffer.put(offset + i, bytes[i]);
        }
    }

    public void setFilled(int bucket) {
        ByteBuffer buffer = getBuffer();
        int offset = bucket * BUCKET_SIZE;
        int flags = buffer.getInt(offset);
        buffer.putInt(offset, flags | BF_FILLED);
    }

    public void setDeleted(int bucket) {
        ByteBuffer buffer = getBuffer();
        int offset = bucket * BUCKET_SIZE;
        int flags = buffer.getInt(offset);
        buffer.putInt(offset, flags | BF_DELETED);
    }

    public void setDatumOffset(int bucket, long doffset) {
        ByteBuffer buffer = getBuffer();
        int offset = bucket * BUCKET_SIZE + DATUM_OFFSET;
        buffer.putLong(offset, doffset);
    }

    public boolean isFilled(int bucket) {
        ByteBuffer buffer = getBuffer();
        int offset = bucket * BUCKET_SIZE;
        int flags = buffer.getInt(offset);
        return (flags & BF_FILLED) != 0;
    }

    public boolean isEmpty(int bucket) {
        return !isFilled(bucket);
    }

    public void getDigest(int bucket, byte[] bytes) {
        int offset = bucket * BUCKET_SIZE + DIGEST_OFFSET;
        get(offset, bytes);
    }

    public boolean isDeleted(int bucket) {
        ByteBuffer buffer = getBuffer();
        int offset = bucket * BUCKET_SIZE;
        int flags = buffer.getInt(offset);
        return (flags & BF_DELETED) != 0;
    }

    public long getDatumOffset(int bucket) {
        ByteBuffer buffer = getBuffer();
        int offset = bucket * BUCKET_SIZE + DATUM_OFFSET;
        return buffer.getLong(offset);
    }
}

