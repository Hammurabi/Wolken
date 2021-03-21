package org.wolkenproject.utils;

public class Null {
    private static final byte[] b_array = new byte[0];

    public static byte[] notNull(byte a[]) {
        if (a == null) {
            return b_array;
        }

        return a;
    }
}
