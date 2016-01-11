package org.pixielib.content;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Repository {
    private BlockIO io;
    private DatumPage page;
    private long dpageno;   // current data page while writing
    private byte ddatum;    // current datum on data page while writing

    Repository() {
        io = new BlockIO();
        page = new DatumPage();
        dpageno = ddatum = 0;
    }

    public void readEvent(long offset, EventBuffer event) throws IOException {
        long pageno = offset / Block.BLOCK_SIZE;
        byte datum = (byte) ((offset - pageno * Block.BLOCK_SIZE) / DatumPage.DATUM_SIZE);

        io.readBlock(pageno, page);

        int totalLength = page.getTotalLength(datum);
        ByteBuffer buffer = ByteBuffer.allocateDirect(totalLength);

        for (; ; ) {
            ByteBuffer data = page.getData(datum);
            buffer.put(data);

            if ((offset = page.getNext(datum)) == 0)
                break;

            pageno = offset / Block.BLOCK_SIZE;
            datum = (byte) ((offset - pageno * Block.BLOCK_SIZE) / DatumPage.DATUM_SIZE);
            io.readBlock(pageno, page);
        }

        buffer.rewind();

        event.set(buffer);
    }

    public void close() throws IOException {
        io.close();
    }

    public void open() throws IOException {
        File file = File.createTempFile("repo-", ".dat");
        file.deleteOnExit();
        io.open(file, "rw");
        io.writeBlock(dpageno, page);
    }

    public long writeEvent(Event event) throws IOException {
        EventBuffer buffer = EventBuffer.makeBuffer(event);
        return writeBuffer(buffer.getBuffer());
    }

    private long writeBuffer(ByteBuffer buffer) throws IOException {
        return writeBytes(buffer.array());
    }

    private long writeBytes(byte[] bytes) throws IOException {
        return writeBytes(bytes, 0, bytes.length);
    }

    private long writeBytes(byte[] bytes, int start, int length) throws IOException {
        long offset = datumoffset();

        io.readBlock(dpageno, page);

        for (int i = 0, j = 0; length > 0; ++i) {
            if (i > 0) {
                page.setNext(ddatum, nextdatumoffset());
                newdatum();
            }

            int nlength = Math.min(length, DatumPage.DATUM_AVAIL);

            page.writeBytes(ddatum, bytes, j + start, nlength);
            page.setLength(ddatum, nlength);
            page.setTotalLength(ddatum, bytes.length);

            length -= nlength;
            j += nlength;
        }

        io.writeBlock(dpageno, page);

        newdatum();

        return offset;
    }

    void newpage() throws IOException {
        page = new DatumPage();
        io.writeBlock(++dpageno, page);
    }

    long datumoffset() {
        return datumoffset(dpageno, ddatum);
    }

    long datumoffset(long pageno, byte datum) {
        return (pageno * Block.BLOCK_SIZE) + (datum * DatumPage.DATUM_SIZE);
    }

    long nextdatumoffset() {
        long pageno = dpageno;
        byte datum = ddatum;

        if ((datum = (byte) ((datum + 1) % DatumPage.DATUM_PER_PAGE)) == 0) {
            pageno++;
        }

        return datumoffset(pageno, datum);
    }

    void newdatum() throws IOException {
        if ((ddatum = (byte) ((ddatum + 1) % DatumPage.DATUM_PER_PAGE)) == 0) {
            io.writeBlock(dpageno, page);
            newpage();
        }
    }

    public void updateEvent(Event event, long offset) {
        EventBuffer buffer = EventBuffer.makeBuffer(event);
        updateBuffer(buffer.getBuffer(), offset);
    }

    private void updateBuffer(ByteBuffer buffer, long offset) {
        updateBuffer(buffer.array(), offset);
    }

    @SuppressWarnings("unused")
    private void updateBuffer(byte[] bytes, long offset) {

    }
}
