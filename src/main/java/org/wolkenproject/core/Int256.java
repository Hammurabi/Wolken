package org.wolkenproject.core;

import org.wolkenproject.utils.Utils;

public class Int256 {
    public int      data[];
    public boolean  signed;

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
}
