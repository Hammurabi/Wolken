package org.wolkenproject.utils;

public class Null {
    private static final byte[] b_array = new byte[0];
    private static final int[]  s_array = new byte[0];
    private static final byte[] c_array = new byte[0];
    private static final byte[] i_array = new byte[0];
    private static final byte[] l_array = new byte[0];

    public static byte[] notNull(byte a[]) {
        if (a == null) {
            return b_array;
        }

        return a;
    }
}
