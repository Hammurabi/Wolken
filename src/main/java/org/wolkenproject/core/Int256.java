package org.wolkenproject.core;

import org.wolkenproject.utils.Utils;

import java.math.BigInteger;
import java.util.Arrays;

public class Int256 {
    public static final Int256 Zero = new Int256(0, false);
    public static final Int256 One  = new Int256(1, false);
    public static final Int256 Two  = new Int256(2, false);
    public static final Int256 Ten  = new Int256(10, false);
    public static final Int256 Max  = new Int256(Utils.fillArray(new byte[32], (byte) 0xFF), false);

    public int      data[];
    public boolean  signed;

    public Int256(long value) {
        this(value, true);
    }

    public Int256(long value, boolean signed) {
        this(convertLong(value), signed);
    }

    public Int256(byte array[], boolean signed) {
        this(convertArray(array), signed);
    }

    private static int[] convertArray(byte[] array) {
        int ints[]  = new int[8];
        ints[0]     = Utils.makeInt(array);
        ints[1]     = Utils.makeInt(array, 4);
        ints[2]     = Utils.makeInt(array, 8);
        ints[3]     = Utils.makeInt(array, 12);
        ints[4]     = Utils.makeInt(array, 16);
        ints[5]     = Utils.makeInt(array, 20);
        ints[6]     = Utils.makeInt(array, 24);
        ints[7]     = Utils.makeInt(array, 28);

        return ints;
    }

    private static int[] convertLong(long value) {
        int result[] = new int[8];
        byte bytes[] = Utils.takeApartLong(value);

        result[6]   = Utils.makeInt(bytes, 0);
        result[7]   = Utils.makeInt(bytes, 4);

        return result;
    }

    private static long convertInts(int result[]) {
        byte bytes0[] = Utils.takeApart(result[6]);
        byte bytes1[] = Utils.takeApart(result[7]);

        return Utils.makeLong(Utils.concatenate(bytes0, bytes1));
    }

    private Int256(int data[], boolean signed) {
        this.data   = data;
        this.signed = signed;
    }

    public Int256 add(Int256 other) {
        int carry       = 0;
        int result[]    = new int[8];

        for (int x = 0; x < 8; x ++) {
            int i = 7 - x;

            for (int b = 0; b < 32; b ++) {
                int bit0    = Utils.getBit(data[i], b);
                int bit1    = Utils.getBit(other.data[i], b);

                int sum0    = (bit0 ^ bit1);
                int sum1    = sum0 ^ carry;
                carry       = (bit0 & bit1) | (carry & sum0);

                result[i]   = Utils.setBit(result[i], b, sum1);
            }
        }

        return new Int256(result, !(other.signed & signed));
    }

    public Int256 mul(Int256 other) {
        int carry       = 0;
        int result[]    = new int[8];

        for (int x = 0; x < 8; x ++) {
            int i = 7 - x;

            for (int b = 0; b < 32; b ++) {
                int bit0    = Utils.getBit(data[i], b);
                int bit1    = Utils.getBit(other.data[i], b);

                int sum0    = (bit0 ^ bit1);
                int sum1    = sum0 ^ carry;
                carry       = (bit0 & bit1) | (carry & sum0);

                result[i]   = Utils.setBit(result[i], b, sum1);
            }
        }

        return new Int256(result, !(other.signed & signed));
    }

    public Int256 sub(Int256 other) {
        int result[]    = new int[8];

        for (int i = 0; i < 8; i ++) {
            result[i]   = ~other.data[i];
        }

        return add(new Int256(result, !(other.signed & signed)));
    }

    public int[] getData() {
        return data;
    }

    public long asLong() {
        return convertInts(data);
    }

    public Int256 shiftr(int n) {
        if (n <= 0) {
            return new Int256(Arrays.copyOf(data, 8), signed);
        }

        switch (n) {
            case 32: return new Int256(new int[]    {0, data[0], data[1], data[2], data[3], data[4], data[5], data[6]}, signed);
            case 64: return new Int256(new int[]    {0, 0, data[0], data[1], data[2], data[3], data[4], data[5]}, signed);
            case 96: return new Int256(new int[]    {0, 0, 0, data[0], data[1], data[2], data[3], data[4]}, signed);
            case 128: return new Int256(new int[]   {0, 0, 0, 0, data[0], data[1], data[2], data[3]}, signed);
            case 160: return new Int256(new int[]   {0, 0, 0, 0, 0, data[0], data[1], data[2]}, signed);
            case 192: return new Int256(new int[]   {0, 0, 0, 0, 0, 0, data[0], data[1]}, signed);
            case 224: return new Int256(new int[]   {0, 0, 0, 0, 0, 0, 0, data[0]}, signed);
            case 256: return Zero;
            default:
            {
                int shift = n / 8;
                int carry = 0;
                int result[] = new int[8];

                for (int i = 0; i < data.length; i ++) {
                    int omit = (0xFFFFFFFF << shift) & data[i];
                    result[i]= (data[i] >> shift) | carry;
                    carry = omit << (32 - shift);
                }

                return new Int256(result, signed);
            }
        }
    }

    @Override
    public String toString() {
        byte array[] = new byte[0];
        for (int i  = 0; i < 8; i ++) {
            array   = Utils.concatenate(array, Utils.takeApart(data[i]));
        }

        // no ternary
        if (signed) {
            return new BigInteger(array).toString();
        } else {
            return new BigInteger(1, array).toString();
        }
    }
}
