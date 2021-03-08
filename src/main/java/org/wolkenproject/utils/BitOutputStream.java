package org.wolkenproject.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitOutputStream extends OutputStream {
    private ByteArrayOutputStream   stream;
    private byte                    current;
    private int                     index;

    public BitOutputStream(byte array[]) {
        this.stream = new ByteArrayOutputStream();
        this.current = 0;
        this.index  = 0;
    }
    
    @Override
    public void write(int i) throws IOException {
    }
}
