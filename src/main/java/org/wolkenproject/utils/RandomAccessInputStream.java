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
        return 0;
    }
}
