package org.wolkenproject.utils;

import org.wolkenproject.encoders.Base16;
import org.wolkenproject.encoders.Base58;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Set;

public class Utils {
    public static final byte[] concatenate(byte[]...arrays)
    {
        int size = 0;
        for(byte[] array : arrays)
            size += array.length;

        byte concatenated[] = new byte[size];

        int index = 0;

        for(byte[] array : arrays)
        {
            System.arraycopy(array, 0, concatenated, index, array.length);

            index += array.length;
        }

        return concatenated;
    }

    public static short makeShort(byte b1, byte b0) {
        return (short) (((b1 & 0xff) <<  8) | ((b0 & 0xff)));
    }

    public static int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (((b3       ) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) <<  8) |
                ((b0 & 0xff)      ));
    }

    public static long makeLong(byte b7, byte b6, byte b5, byte b4,
                                byte b3, byte b2, byte b1, byte b0)
    {
        return ((((long)b7       ) << 56) |
                (((long)b6 & 0xff) << 48) |
                (((long)b5 & 0xff) << 40) |
                (((long)b4 & 0xff) << 32) |
                (((long)b3 & 0xff) << 24) |
                (((long)b2 & 0xff) << 16) |
                (((long)b1 & 0xff) <<  8) |
                (((long)b0 & 0xff)      ));
    }

    public static int makeInt24(byte[] trim) {
        return makeInt((byte) 0, trim[0], trim[1], trim[2]);
    }

    public static int makeInt(byte[] trim) {
        return makeInt(trim[0], trim[1], trim[2], trim[3]);
    }

    public static int makeInt(byte[] trim, int off) {
        return makeInt(trim[off], trim[off + 1], trim[off + 2], trim[off + 3]);
    }

    public static long makeLong(byte[] trim) {
        if (trim.length < 8) {
            trim = concatenate(new byte[8 - trim.length], trim);
        }
        return makeLong(trim[0], trim[1], trim[2], trim[3], trim[4], trim[5], trim[6], trim[7]);
    }

    public static long makeLongUnsafe(byte[] trim) {
        return makeLong(trim[0], trim[1], trim[2], trim[3], trim[4], trim[5], trim[6], trim[7]);
    }

    public static byte[] trim(byte[] bytes, int offset, int length) {
        byte new_bytes[]    = new byte[length];

        int free            = 0;

        for(int index = offset; index < (offset + length); index ++)
            new_bytes[free ++] = bytes[index];
        return new_bytes;
    }

    public static boolean equals(byte[] trim, byte[] trim1) {
        if(trim.length != trim1.length) return false;

        for(int i = 0; i < trim1.length; i ++)
            if(trim[i] != trim1[i]) return false;

        return true;
    }

    public static byte[] int24(int i)
    {
        return new byte[] {
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >>  8) & 0xFF),
                (byte) ((i) & 0xFF)};
    }

    public static byte[] appendIntegerToBytes(byte data[], int integer)
    {
        return concatenate(data, new byte[] {
                (byte) ((integer >> 24) & 0xFF),
                (byte) ((integer >> 16) & 0xFF),
                (byte) ((integer >> 8) & 0xFF),
                (byte) ((integer) & 0xFF)});
    }

    public static long getTimeHours(int numHours, short mins, short seconds)
    {
        return (numHours * (3600) + (mins * 60) + seconds) * 1000L;
    }

    public static long getTimeHours(int numHours)
    {
        return numHours * 3600_000L;
    }

    public static long getTimeMinutes(int numMinutes)
    {
        return numMinutes * 60_000L;
    }

    public static byte[] reverseBytes(byte[] bytes) {
        byte[] buf = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            buf[i] = bytes[bytes.length - 1 - i];
        return buf;
    }

    public static byte[] takeApartShort(short integer) {
        return new byte[] {
                (byte) ((integer >> 8) & 0xFF),
                (byte) ((integer) & 0xFF)};
    }

    public static byte[] takeApartChar(char integer) {
        return new byte[] {
                (byte) ((integer >> 8) & 0xFF),
                (byte) ((integer) & 0xFF)};
    }

    public static byte[] takeApart(int integer) {
        return new byte[] {
                (byte) ((integer >> 24) & 0xFF),
                (byte) ((integer >> 16) & 0xFF),
                (byte) ((integer >> 8) & 0xFF),
                (byte) ((integer) & 0xFF)};
    }

    public static byte[] appendLongToBytes(byte[] data, long integer) {
        return concatenate(data, new byte[] {
                (byte) ((integer >> 56) & 0xFF),
                (byte) ((integer >> 48) & 0xFF),
                (byte) ((integer >> 40) & 0xFF),
                (byte) ((integer >> 32) & 0xFF),
                (byte) ((integer >> 24) & 0xFF),
                (byte) ((integer >> 16) & 0xFF),
                (byte) ((integer >>  8) & 0xFF),
                (byte) ((integer) & 0xFF)});
    }

    public static byte[] takeApartLong(long integer)
    {
        return new byte[] {
                (byte) ((integer >> 56) & 0xFF),
                (byte) ((integer >> 48) & 0xFF),
                (byte) ((integer >> 40) & 0xFF),
                (byte) ((integer >> 32) & 0xFF),
                (byte) ((integer >> 24) & 0xFF),
                (byte) ((integer >> 16) & 0xFF),
                (byte) ((integer >>  8) & 0xFF),
                (byte) ((integer) & 0xFF)};
    }

    public static byte[] fromSet(Set<byte[]> hashes) {
        byte bytes[] = new byte[0];

        for (byte [] data : hashes) bytes = concatenate(bytes, data);

        return bytes;
    }

    public static byte[] takeApart(char[] password) {
        ByteBuffer buffer = ByteBuffer.allocate(password.length * 2);
        for (char chr : password) buffer.putChar(chr);
        buffer.flip();

        return buffer.array();
    }public static byte[] fillArray(byte[] chars, byte c) {
        Arrays.fill(chars, c);
        return chars;
    }

    public static char[] fillArray(char[] chars, char c) {
        Arrays.fill(chars, c);
        return chars;
    }

    public static char[] makeChars(byte[] trim) {
        ByteBuffer charBuffer = ByteBuffer.allocate(trim.length);
        charBuffer.put(trim);
        charBuffer.flip();
        char chars[] = new char[trim.length / 2];
        for (int i = 0; i < chars.length; i ++) {
            chars[i] = charBuffer.getChar();
        }
        return chars;
    }

    public static boolean empty(byte[] data) {
        for (byte b : data) {
            if (b != 0) {
                return false;
            }
        }

        return true;
    }

    public static byte[] decodeString(String encoded) {
        if (Base58.isEncoded(encoded)) {
            return Base58.decode(encoded);
        } else if (Base16.isEncoded(encoded)) {
            return Base16.decode(encoded);
        }

        return null;
    }

    public static void writeInt(int integer, OutputStream stream) throws IOException {
        stream.write((byte) ((integer >> 24) & 0xFF));
        stream.write((byte) ((integer >> 16) & 0xFF));
        stream.write((byte) ((integer >> 8) & 0xFF));
        stream.write((byte) ((integer) & 0xFF));
    }

    public static void writeLong(long integer, OutputStream stream) throws IOException {
        stream.write((byte) ((integer >> 56) & 0xFF));
        stream.write((byte) ((integer >> 48) & 0xFF));
        stream.write((byte) ((integer >> 40) & 0xFF));
        stream.write((byte) ((integer >> 32) & 0xFF));
        stream.write((byte) ((integer >> 24) & 0xFF));
        stream.write((byte) ((integer >> 16) & 0xFF));
        stream.write((byte) ((integer >>  8) & 0xFF));
        stream.write((byte) ((integer) & 0xFF));
    }

    public static void writeUnsignedInt16(int integer, OutputStream stream) throws IOException {
        stream.write((byte) ((integer >> 8) & 0xFF));
        stream.write((byte) ((integer) & 0xFF));
    }

    public static int timestampInSeconds() {
        return (int) (System.currentTimeMillis() / 1000) - 1048035600;
    }

    public static byte[] pad(int padCount, byte[] bytes) {
        return concatenate(new byte[padCount], bytes);
    }

    public static byte[] pad(int padCount, int padValue, byte[] bytes) {
        return concatenate(fillArray(new byte[padCount], (byte) padValue), bytes);
    }

    public static int getBit(int byt, int position)
    {
        return (byt >> position) & 1;
    }

    public static int setBit(byte byt, int position)
    {
        return byt | 1 << position;
    }

    public static int clearBit(byte byt, int position)
    {
        return byt & ~(1 << position);
    }

    public static int toggleBit(byte byt, int position)
    {
        return byt ^ ~(1 << position);
    }

    public static int setBit(byte byt, int position, int value)
    {
        return byt ^= (-(value & 1) ^ byt) & (1 << position);
    }

    public static int makeByte(byte[] buffer) {
        return Byte.toUnsignedInt((byte) setBit((byte) setBit((byte) setBit((byte) setBit((byte) setBit((byte) setBit((byte) setBit((byte) setBit((byte) 0, 7, buffer[7]), 6, buffer[6]), 5, buffer[5]), 4, buffer[4]), 3, buffer[3]), 2, buffer[2]), 1, buffer[1]), 0, buffer[0]));
    }
}
