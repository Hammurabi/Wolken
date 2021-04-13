package org.wolkenproject.utils;

import java.util.Arrays;

public class ByteArray {
    private final byte array[];

    public ByteArray(byte hash[]) {
        this.array = hash;
    }

    public byte[] getArray() {
        return array;
    }

    public int length() {
        return length();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArray byteArray = (ByteArray) o;
        return Arrays.equals(array, byteArray.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    public static final ByteArray wrap(byte array[]) {
        return new ByteArray(array);
    }
}
