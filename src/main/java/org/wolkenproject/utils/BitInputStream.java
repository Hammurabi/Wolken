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
        int location = index ++;
        if ((location * 8) >= array.length) {
            return -1;
        }

        int byteAddress = location / 8;
        int bitAddress  = location % 8;

        return Utils.getBit(array[location], bitAddress);
    }

    public int readByte() throws IOException {
        byte buffer[] = new byte[8];

        int read = read(buffer);
        if (read < 8) {
            throw new IOException("not enough bits remaining.");
        }

        return Utils.makeByte(buffer);
    }

    public boolean hasRemaining() {
    }
}
