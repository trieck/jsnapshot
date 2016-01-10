package org.pixielib.content;

import java.nio.ByteBuffer;

public class DatumPage extends Block {

    /**
     * Each datum page stores up to 8 512-byte datums.
     * <p>
     * The structure of a datum is defined by:
     * <p>
     * NEXT-PTR     : pointer to next datum page if linked (8-bytes)
     * TOTAL-LENGTH : total length of data for datum (4-bytes)
     * LENGTH       : length of data for this datum segment (4-bytes)
     * DATA         : up to 496-bytes of data
     */
    public static final int DATUM_SIZE = 512;
    public static final int DATUM_PER_PAGE = Block.BLOCK_SIZE / DATUM_SIZE;
    public static final int DATUM_AVAIL = DATUM_SIZE - 16;
    private static final int TOTAL_LENGTH_OFFSET = 8;
    private static final int LENGTH_OFFSET = 12;
    private static final int DATA_OFFSET = 16;

    public void setNext(byte datum, long next) {
        ByteBuffer buffer = getBuffer();
        int offset = datum * DATUM_SIZE;
        buffer.putLong(offset, next);
    }

    public void setLength(byte datum, int length) {
        ByteBuffer buffer = getBuffer();
        int offset = datum * DATUM_SIZE + LENGTH_OFFSET;
        buffer.putInt(offset, length);
    }

    public void setTotalLength(byte datum, int length) {
        ByteBuffer buffer = getBuffer();
        int offset = datum * DATUM_SIZE + TOTAL_LENGTH_OFFSET;
        buffer.putInt(offset, length);
    }

    public void writeBytes(byte datum, byte[] bytes, int start, int length) {
        ByteBuffer buffer = getBuffer();
        int offset = datum * DATUM_SIZE + DATA_OFFSET;
        buffer.position(offset);
        buffer.put(bytes, start, length);
    }
}
