package org.wolkenproject.utils;

import java.io.IOException;
import java.io.InputStream;

public class RandomAccessInputStream extends InputStream {
    private byte    buffer[];
    private int     index;

    public RandomAccessInputStream(byte buffer[], int index) {
        this.buffer = buffer;
        this.index  = index;
    }

    @Override
    public int read() throws IOException {
        return checkBounds(index ++);
    }

    public short readShort() throws IOException {
        return Utils.makeShort(read(), read());
    }

    public char readChar() throws IOException {
        return Utils.makeChar(read(), read());
    }

    public int readInt() throws IOException {
        return Utils.makeInt(read(), read(), read(), read());
    }

    public long readLong() throws IOException {
        return Utils.makeLong(read(), read(), read(), read(), read(), read(), read(), read());
    }

    private int checkBounds(int index) throws IOException {
        if (index >= buffer.length) {
            throw new IOException("end of buffer reached.");
        }

        return Byte.toUnsignedInt(buffer[index]);
    }

    public short readShort(int index) throws IOException {
        return Utils.makeShort(read(), read());
    }

    public char readChar(int index) throws IOException {
        return Utils.makeChar(read(), read());
    }

    public int readInt(int index) throws IOException {
        return Utils.makeInt(read(), read(), read(), read());
    }

    public long readLong(int index) throws IOException {
        return Utils.makeLong(read(), read(), read(), read(), read(), read(), read(), read());
    }
}
