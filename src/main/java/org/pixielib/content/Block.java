package org.pixielib.content;

import java.nio.ByteBuffer;

public class Block {
    public static final int BLOCK_SIZE = 4096;

    private final ByteBuffer buffer;

    public Block() {
        buffer = ByteBuffer.allocateDirect(BLOCK_SIZE);
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void rewind() {
        buffer.rewind();
    }

    public void flip() {
        buffer.flip();
    }

    public void get(int offset, byte[] bytes) {
        buffer.position(offset);
        buffer.get(bytes);
        buffer.rewind();
    }
}
