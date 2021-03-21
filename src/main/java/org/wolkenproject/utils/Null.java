package org.wolkenproject.utils;

public class Null {
    private static final byte[]    b_array = new byte[0];
    private static final short[]   s_array = new short[0];
    private static final char[]    c_array = new char[0];
    private static final int[]     i_array = new int[0];
    private static final long[]    l_array = new long[0];

    public static byte[] notNull(byte a[]) {
        if (a == null) {
            return b_array;
        }

        return a;
    }

    public static short[] notNull(short a[]) {
        if (a == null) {
            return s_array;
        }

        return a;
    }
}
