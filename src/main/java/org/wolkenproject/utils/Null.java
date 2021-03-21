package org.wolkenproject.utils;

public class Null {
    private static final byte[]     b_array = new byte[0];
    private static final int[]      s_array = new int[0];
    private static final short[]    c_array = new short[0];
    private static final char[]     i_array = new char[0];
    private static final long[]     l_array = new long[0];

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
