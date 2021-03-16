package org.wolkenproject.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// this class represents an UNSIGNED variable integer
// that has a range of 1 - 8 bytes
public class VarInt {
    // write a uint32 to stream, or uint30 if !fullBitsNeeded
    public static void writeCompactUInt32(long integer, boolean preserveAllBits, OutputStream stream) throws IOException {
        long bits = Utils.numBitsRequired(integer);

        if (preserveAllBits) {
            if (bits <= 8) {
                stream.write(0);
                stream.write((int) integer);
            } else if (bits <= 16) {
                stream.write(1);
                byte bytes[] = Utils.takeApartShort(integer);
                stream.write(((bytes[0])));
                stream.write(((bytes[1])));
            } else if (bits <= 24) {
                stream.write(2);
                byte bytes[] = Utils.takeApartInt24(integer);
                stream.write(((bytes[0])));
                stream.write(((bytes[1])));
                stream.write(((bytes[2])));
            } else if (bits <= 32) {
                stream.write(3);
                byte bytes[] = Utils.takeApart(integer);
                stream.write(bytes[0]);
                stream.write(bytes[1]);
                stream.write(bytes[2]);
                stream.write(bytes[3]);
            }
        } else {
            if (bits <= 6) {
                stream.write((int) (integer & 0x3F));
            } else if (bits <= 14) {
                byte bytes[] = Utils.takeApartShort(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x3F) | 1 << 6);
                stream.write((Byte.toUnsignedInt(bytes[1])));
            } else if (bits <= 22) {
                byte bytes[] = Utils.takeApartInt24(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x3F) | 2 << 6);
                stream.write((Byte.toUnsignedInt(bytes[1])));
                stream.write((Byte.toUnsignedInt(bytes[2])));
            } else if (bits <= 30) {
                byte bytes[] = Utils.takeApart(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x3F) | 3 << 6);
                stream.write((Byte.toUnsignedInt(bytes[1])));
                stream.write((Byte.toUnsignedInt(bytes[2])));
                stream.write((Byte.toUnsignedInt(bytes[3])));
            }
        }
    }

    public static void writeCompactUInt64(long integer, boolean preserveAllBits, OutputStream stream) throws IOException {
        long bits = Utils.numBitsRequired(integer);

        if (preserveAllBits) {
            if (bits <= 8) {
                stream.write(0);
                stream.write((int) integer);
            } else if (bits <= 16) {
                stream.write(1);
                byte bytes[] = Utils.takeApartShort(integer);
                stream.write(((bytes[0])));
                stream.write(((bytes[1])));
            } else if (bits <= 24) {
                stream.write(2);
                byte bytes[] = Utils.takeApartInt24(integer);
                stream.write(((bytes[0])));
                stream.write(((bytes[1])));
                stream.write(((bytes[2])));
            } else if (bits <= 32) {
                stream.write(3);
                byte bytes[] = Utils.takeApart(integer);
                stream.write(bytes[0]);
                stream.write(bytes[1]);
                stream.write(bytes[2]);
                stream.write(bytes[3]);
            } else if (bits <= 40) {
                stream.write(4);
                byte bytes[] = Utils.takeApartInt40(integer);
                stream.write(bytes[0]);
                stream.write(bytes[1]);
                stream.write(bytes[2]);
                stream.write(bytes[3]);
                stream.write(bytes[4]);
            } else if (bits <= 48) {
                stream.write(5);
                byte bytes[] = Utils.takeApartInt48(integer);
                stream.write(bytes[0]);
                stream.write(bytes[1]);
                stream.write(bytes[2]);
                stream.write(bytes[3]);
                stream.write(bytes[4]);
                stream.write(bytes[5]);
            } else if (bits <= 56) {
                stream.write(6);
                byte bytes[] = Utils.takeApartInt48(integer);
                stream.write(bytes[0]);
                stream.write(bytes[1]);
                stream.write(bytes[2]);
                stream.write(bytes[3]);
                stream.write(bytes[4]);
                stream.write(bytes[5]);
                stream.write(bytes[6]);
            }
        } else {
            if (bits <= 5) {
                stream.write((int) (integer & 0x1F));
            } else if (bits <= 13) {
                byte bytes[] = Utils.takeApartShort(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x1F) | 1 << 5);
                stream.write((Byte.toUnsignedInt(bytes[1])));
            } else if (bits <= 21) {
                byte bytes[] = Utils.takeApartInt24(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x1F) | 2 << 5);
                stream.write((Byte.toUnsignedInt(bytes[1])));
                stream.write((Byte.toUnsignedInt(bytes[2])));
            } else if (bits <= 29) {
                byte bytes[] = Utils.takeApart(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x1F) | 3 << 5);
                stream.write((Byte.toUnsignedInt(bytes[1])));
                stream.write((Byte.toUnsignedInt(bytes[2])));
                stream.write((Byte.toUnsignedInt(bytes[3])));
            } else if (bits <= 37) {
                byte bytes[] = Utils.takeApartInt40(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x1F) | 4 << 5);
                stream.write((Byte.toUnsignedInt(bytes[1])));
                stream.write((Byte.toUnsignedInt(bytes[2])));
                stream.write((Byte.toUnsignedInt(bytes[3])));
                stream.write((Byte.toUnsignedInt(bytes[4])));
            } else if (bits <= 45) {
                byte bytes[] = Utils.takeApartInt48(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x1F) | 5 << 5);
                stream.write((Byte.toUnsignedInt(bytes[1])));
                stream.write((Byte.toUnsignedInt(bytes[2])));
                stream.write((Byte.toUnsignedInt(bytes[3])));
                stream.write((Byte.toUnsignedInt(bytes[4])));
                stream.write((Byte.toUnsignedInt(bytes[5])));
            } else if (bits <= 53) {
                byte bytes[] = Utils.takeApartInt56(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x1F) | 6 << 5);
                stream.write((Byte.toUnsignedInt(bytes[1])));
                stream.write((Byte.toUnsignedInt(bytes[2])));
                stream.write((Byte.toUnsignedInt(bytes[3])));
                stream.write((Byte.toUnsignedInt(bytes[4])));
                stream.write((Byte.toUnsignedInt(bytes[5])));
                stream.write((Byte.toUnsignedInt(bytes[6])));
            } else if (bits <= 61) {
                byte bytes[] = Utils.takeApartLong(integer);
                stream.write((Byte.toUnsignedInt(bytes[0]) & 0x1F) | 7 << 5);
                stream.write((Byte.toUnsignedInt(bytes[1])));
                stream.write((Byte.toUnsignedInt(bytes[2])));
                stream.write((Byte.toUnsignedInt(bytes[3])));
                stream.write((Byte.toUnsignedInt(bytes[4])));
                stream.write((Byte.toUnsignedInt(bytes[5])));
                stream.write((Byte.toUnsignedInt(bytes[6])));
                stream.write((Byte.toUnsignedInt(bytes[7])));
            }
        }
    }

    public static int readCompactUInt32(boolean preserveAllBits, InputStream stream) throws IOException {
        if (preserveAllBits) {
            int numBytes = stream.read();
            byte bytes[] = new byte[numBytes + 1];
            stream.read(bytes);

            return Utils.makeInt(Utils.conditionalExpand(4, bytes));
        } else {
            int test    = stream.read();
            int value   = test & 0x3F;
            int length  = test >> 6;
            if (length == 0) {
                return value;
            }

            byte remaining[] = new byte[length];
            if (stream.read(remaining) != remaining.length) {
                throw new IOException();
            }

            System.out.println(value + " " + length + " " + Utils.makeInt(Utils.conditionalExpand(4, Utils.concatenate(new byte[] {(byte) value}, remaining))));

            return Utils.makeInt(Utils.conditionalExpand(4, Utils.concatenate(new byte[] {(byte) value}, remaining)));
        }
    }

    public static long readCompactUInt64(boolean preserveAllBits, InputStream stream) throws IOException {
        if (preserveAllBits) {
            int numBytes = stream.read();
            byte bytes[] = new byte[numBytes + 1];
            stream.read(bytes);

            return Utils.makeInt(Utils.conditionalExpand(8, bytes));
        } else {
            int test    = stream.read();
            int value   = test & 0x1F;
            int length  = test >> 5;
            if (length == 0) {
                return value;
            }

            byte remaining[] = new byte[length];
            stream.read(remaining);

            return Utils.makeInt(Utils.conditionalExpand(8, Utils.concatenate(new byte[] {(byte) value}, remaining)));
        }
    }
}
