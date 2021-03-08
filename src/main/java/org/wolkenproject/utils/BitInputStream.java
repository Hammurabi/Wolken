package org.wolkenproject.utils;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream extends InputStream {
    private byte    array[];
    private int     index;

    public BitInputStream(byte array[]) {
        this.array  = array;
        this.index  = 0;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
