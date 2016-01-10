package org.pixielib.content;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BlockIO {

    private RandomAccessFile file;

    public void close() throws IOException {
        if (file != null) {
            file.close();
        }
    }

    public void open(File f, String mode) throws IOException {
        close();
        file = new RandomAccessFile(f, mode);
    }

    public void open(String filename, String mode) throws IOException {
        close();
        file = new RandomAccessFile(filename, mode);
    }

    public long tell() throws IOException {
        return file.getFilePointer();
    }

    public long getFileSize() throws IOException {
        return file.length();
    }

    public void readBlock(long blockno, Block block) throws IOException {

        seekBlock(blockno);

        if ((file.getChannel().read(block.getBuffer())) != Block.BLOCK_SIZE)
            throw new IOException("unable to read block.");

        block.flip();
    }

    public void seekBlock(long blockno) throws IOException {
        file.seek(blockno * Block.BLOCK_SIZE);
    }

    public void writeBlock(long blockno, Block block) throws IOException {
        seekBlock(blockno);

        if ((file.getChannel().write(block.getBuffer())) != Block.BLOCK_SIZE)
            throw new IOException("unable to write block.");
    }
}
