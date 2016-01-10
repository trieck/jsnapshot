package org.pixielib.content;

import java.nio.ByteBuffer;

public class BucketPage extends Block {

    public static final int SHA1_DIGEST_BYTES = 20;
    private static final int BF_FILLED = 1;
    private static final int BF_DELETED = 1 << 1;
    private static final int BUCKET_SIZE = 32;
    public static final int BUCKETS_PER_PAGE = Block.BLOCK_SIZE / BUCKET_SIZE;
    private static final int KEY_OFFSET = 12;
    private static final int DATUM_OFFSET = 4;

    public void setKey(int bucket, byte[] bytes) {
        ByteBuffer buffer = getBuffer();

        int offset = bucket * BUCKET_SIZE + KEY_OFFSET;
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

    public void setDatumOffset(int bucket, long doffset) {
        ByteBuffer buffer = getBuffer();
        int offset = bucket * BUCKET_SIZE + DATUM_OFFSET;
        buffer.putLong(offset, doffset);
    }

    public boolean isFilled(int bucket) {
        return false;
    }

    public void getDigest(int bucket, byte[] bytes) {
        ByteBuffer buffer = getBuffer();
        int offset = bucket * BUCKET_SIZE + KEY_OFFSET;
        buffer.get(bytes, offset, SHA1_DIGEST_BYTES);
    }
}
