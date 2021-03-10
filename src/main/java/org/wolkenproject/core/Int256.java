package org.wolkenproject.core;

import org.wolkenproject.utils.Utils;

public class Int256 {
    public int      data[];
    public boolean  signed;

    public Int256(long value) {
        this(value, true);
    }

    public Int256(long value, boolean signed) {
        this(convertLong(value), signed);
    }

    private static int[] convertLong(long value) {
        int result[] = new int[8];
        byte bytes[] = Utils.takeApartLong(value);

        result[0]   = Utils.makeInt(bytes, 0);
        result[1]   = Utils.makeInt(bytes, 4);

        return result;
    }

    private Int256(int data[], boolean signed) {
        this.data   = data;
        this.signed = signed;
    }

    public Int256 add(Int256 other) {
        int carry       = 0;
        int result[]    = new int[8];

        for (int i = 0; i < 4; i ++) {
            for (int b = 0; b < 32; b ++) {
                int bit0    = Utils.getBit(data[i], b);
                int bit1    = Utils.getBit(other.data[i], b);

                int sum0    = (bit0 ^ bit1);
                int sum1    = sum0 ^ carry;
                carry       = ((bit0 & bit1) & sum1) | carry;

                result[i]   = Utils.setBit(result[i], b, sum1);
            }
        }

        return new Int256(result, !(other.signed & signed));
    }

    public Int256 sub(Int256 other) {
        int result[]    = new int[8];

        for (int i = 0; i < 4; i ++) {
            result[i]   = ~other.data[i];
        }

        return add(new Int256(result, !(other.signed & signed)));
    }
}
