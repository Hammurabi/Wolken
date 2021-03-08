package org.wolkenproject.utils;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream extends InputStream {
    private byte    array[];
    private int     index;

    @Override
    public int read() throws IOException {
        return 0;
    }
}
