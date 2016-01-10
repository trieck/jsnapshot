package org.pixielib.content;

import java.nio.ByteBuffer;

public class Block {
    public static final int BLOCK_SIZE = 4096;

    private ByteBuffer buffer;

    public Block() {
        buffer = ByteBuffer.allocateDirect(BLOCK_SIZE);
    }

    public void set(ByteBuffer buffer) {
        this.buffer.put(buffer);
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
}
