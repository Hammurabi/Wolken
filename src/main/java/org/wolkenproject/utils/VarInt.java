package org.wolkenproject.utils;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;

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
            } else if (bits <= 64) {
                stream.write(7);
                byte bytes[] = Utils.takeApartInt48(integer);
                stream.write(bytes[0]);
                stream.write(bytes[1]);
                stream.write(bytes[2]);
                stream.write(bytes[3]);
                stream.write(bytes[4]);
                stream.write(bytes[5]);
                stream.write(bytes[6]);
                stream.write(bytes[7]);
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
            if (stream.read(bytes) != bytes.length) {
                throw new IOException();
            }

            return Utils.makeInt(Utils.conditionalExpand(4, bytes));
        } else {
            int test    = stream.read();
            int value   = test & 0x3F;
            int length  = test >>> 6;
            if (length == 0) {
                return value;
            }

            byte remaining[] = new byte[length];
            if (stream.read(remaining) != remaining.length) {
                throw new IOException();
            }

            return Utils.makeInt(Utils.conditionalExpand(4, Utils.concatenate(new byte[] {(byte) value}, remaining)));
        }
    }

    public static long readCompactUInt64(boolean preserveAllBits, InputStream stream) throws IOException {
        if (preserveAllBits) {
            int numBytes = stream.read();
            byte bytes[] = new byte[numBytes + 1];
            if (stream.read(bytes) != bytes.length) {
                throw new IOException();
            }

            return Utils.makeInt(Utils.conditionalExpand(4, bytes));
        } else {
            int test    = stream.read();
            int value   = test & 0x1F;
            int length  = test >>> 5;

            if (length == 0) {
                return value;
            }

            byte remaining[] = new byte[length];
            if (stream.read(remaining) != remaining.length) {
                throw new IOException();
            }

            return Utils.makeLong(Utils.conditionalExpand(8, Utils.concatenate(new byte[] {(byte) value}, remaining)));
        }
    }

    public static int sizeOfCompactUin32(int integer, boolean preserveAllBits) {
        long bits = Utils.numBitsRequired(integer);

        if (preserveAllBits) {
            if (bits <= 8) {
                return 2;
            } else if (bits <= 16) {
                return 3;
            } else if (bits <= 24) {
                return 4;
            } else if (bits <= 32) {
                return 5;
            }
        } else {
            if (bits <= 6) {
                return 1;
            } else if (bits <= 14) {
                return 2;
            } else if (bits <= 22) {
                return 3;
            } else if (bits <= 30) {
                return 4;
            }
        }

        return 0;
    }

    public static int sizeOfCompactUin64(long integer, boolean preserveAllBits) {
        long bits = Utils.numBitsRequired(integer);

        if (preserveAllBits) {
            if (bits <= 8) {
                return 2;
            } else if (bits <= 16) {
                return 3;
            } else if (bits <= 24) {
                return 4;
            } else if (bits <= 32) {
                return 5;
            } else if (bits <= 40) {
                return 6;
            } else if (bits <= 48) {
                return 7;
            } else if (bits <= 56) {
                return 8;
            } else if (bits <= 64) {
                return 9;
            }
        } else {
            if (bits <= 5) {
                return 1;
            } else if (bits <= 13) {
                return 2;
            } else if (bits <= 21) {
                return 3;
            } else if (bits <= 29) {
                return 4;
            } else if (bits <= 37) {
                return 5;
            } else if (bits <= 45) {
                return 6;
            } else if (bits <= 53) {
                return 7;
            } else if (bits <= 61) {
                return 8;
            }
        }

        return 0;
    }

    // writes an unsigned 256 bit integer to stream
    // when preserveAllBits is true, then the resulting
    // integer will only be 251 bits in length as 5 bits
    // will be used to encode the length of the integer.
    public static void writeCompactUint128(BigInteger integer, boolean preserveAllBits, OutputStream stream) throws WolkenException, IOException {
        byte bytes[]    = integer.toByteArray();
        int drop = 0;
        for (int i = 0; i < bytes.length; i ++) {
            if (bytes[i] == 0) {
                drop ++;
            }
        }

        if (drop > 0) {
            byte temp[] = new byte[bytes.length - drop];
            System.arraycopy(bytes, drop, temp, 0, temp.length);
            bytes = temp;
        }

        if (bytes.length > 16) {
            throw new WolkenException("writeCompactUint128 only allows up to  2^128 bits.");
        }

        int length = bytes.length;
        if (preserveAllBits) {
            stream.write(length);
            stream.write(bytes);
        } else {
            bytes[0] = (byte) (bytes[0] & 0xF | (length - 1) << 4);
            stream.write(bytes);
        }
    }

    public static BigInteger readCompactUint128(boolean preserveAllBits, InputStream stream) throws WolkenException, IOException {
        if (preserveAllBits) {
            int length      = SerializableI.checkNotEOF(stream.read());
            byte bytes[]    = new byte[length];
            SerializableI.checkFullyRead(stream.read(bytes), length);

            return new BigInteger(1, bytes);
        } else {
            int firstByte   = SerializableI.checkNotEOF(stream.read());
            int length      = firstByte >>> 4;
            if (length > 0) {
                byte bytes[]    = new byte[length + 1];
                SerializableI.checkFullyRead(stream.read(bytes, 1, length), length);

                bytes[0]        = (byte) (firstByte & 0xF);
                return new BigInteger(1, bytes);
            } else {
                return new BigInteger(1, new byte[] { (byte) (firstByte & 0xF) });
            }
        }
    }

    public static int sizeOfCompactUint128(BigInteger integer, boolean preserveAllBits) {
        byte bytes[]    = integer.toByteArray();
        int drop = 0;
        for (int i = 0; i < bytes.length; i ++) {
            if (bytes[i] == 0) {
                drop ++;
            }
        }

        if (drop > 0) {
            byte temp[] = new byte[bytes.length - drop];
            System.arraycopy(bytes, drop, temp, 0, temp.length);
            bytes = temp;
        }

        if (bytes.length > 16) {
            return 0;
        }

        if (preserveAllBits) {
            return bytes.length + 1;
        } else {
            return bytes.length;
        }
    }

    // writes an unsigned 256 bit integer to stream
    // when preserveAllBits is true, then the resulting
    // integer will only be 251 bits in length as 5 bits
    // will be used to encode the length of the integer.
    public static void writeCompactUint256(BigInteger integer, boolean preserveAllBits, OutputStream stream) throws WolkenException, IOException {
        byte bytes[]    = integer.toByteArray();
        int drop = 0;
        for (int i = 0; i < bytes.length; i ++) {
            if (bytes[i] == 0) {
                drop ++;
            }
        }

        if (drop > 0) {
            byte temp[] = new byte[bytes.length - drop];
            System.arraycopy(bytes, drop, temp, 0, temp.length);
            bytes = temp;
        }

        if (bytes.length > 32) {
            throw new WolkenException("writeCompactUint32 only allows up to  2^256 bits.");
        }

        int length = bytes.length;
        if (preserveAllBits) {
            stream.write(length);
            stream.write(bytes);
        } else {
            bytes[0] = (byte) (bytes[0] & 0x7 | (length - 1) << 3);
            stream.write(bytes);
        }
    }

    public static BigInteger readCompactUint256(boolean preserveAllBits, InputStream stream) throws WolkenException, IOException {
        if (preserveAllBits) {
            int length      = SerializableI.checkNotEOF(stream.read());
            byte bytes[]    = new byte[length];
            SerializableI.checkFullyRead(stream.read(bytes), length);

            return new BigInteger(1, bytes);
        } else {
            int firstByte   = SerializableI.checkNotEOF(stream.read());
            int length      = firstByte >>> 3;
            if (length > 0) {
                byte bytes[]    = new byte[length + 1];
                SerializableI.checkFullyRead(stream.read(bytes, 1, length), length);

                bytes[0]        = (byte) (firstByte & 0x7);
                return new BigInteger(1, bytes);
            } else {
                return new BigInteger(1, new byte[] { (byte) (firstByte & 0x7) });
            }
        }
    }

    public static int sizeOfCompactUint256(BigInteger integer, boolean preserveAllBits) {
        byte bytes[]    = integer.toByteArray();
        int drop = 0;
        for (int i = 0; i < bytes.length; i ++) {
            if (bytes[i] == 0) {
                drop ++;
            }
        }

        if (drop > 0) {
            byte temp[] = new byte[bytes.length - drop];
            System.arraycopy(bytes, drop, temp, 0, temp.length);
            bytes = temp;
        }

        if (bytes.length > 32) {
            return 0;
        }

        if (preserveAllBits) {
            return bytes.length + 1;
        } else {
            return bytes.length;
        }
    }
}
