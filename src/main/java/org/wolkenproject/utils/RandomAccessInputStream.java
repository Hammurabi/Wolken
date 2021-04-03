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
        int nextIndex = index ++;

        if (nextIndex >= buffer.length) {
            throw new IOException("end of buffer reached.");
        }

        return Byte.toUnsignedInt(buffer[nextIndex]);
    }

    public int readInt() throws IOException {
        return Utils.makeInt(read(), read(), read(), read());
    }

    public int readLong() throws IOException {
        return Utils.makeLong(read(), read(), read(), read(), read(), read(), read(), read());
    }
}
