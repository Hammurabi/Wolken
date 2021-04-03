package org.wolkenproject.utils;

import java.io.IOException;
import java.io.InputStream;

public class RandomAccessInputStream extends InputStream {
    private byte buffer[];

    public RandomAccessInputStream(byte buffer[]) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
