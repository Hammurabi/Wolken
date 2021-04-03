package org.wolkenproject.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

public class HashUtil {
    public static byte[] sha256(byte data[])
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return hash;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static byte[] sha1(byte data[])
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(data);
            return hash;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static byte[] sha3(byte data[])
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA3-256", "BC");
            byte[] hash = digest.digest(data);
            return hash;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] sha512(byte data[]) {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-512", "BC");
            byte[] hash = digest.digest(data);
            return hash;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static byte[] sha256d(byte data[])
    {
        return sha256(sha256(data));
    }

    public static byte[] sha3d(byte data[])
    {
        return sha3(sha3(data));
    }

    public static byte[] sha512d(byte data[])
    {
        return sha512(sha512(data));
    }

    public static byte[] hmacsha512(byte[] key, byte[] data)
    {
        try
        {
            SHA512Digest digest = new SHA512Digest();
            HMac hMac = new HMac(digest);
            hMac.init(new KeyParameter(key));

            hMac.reset();
            hMac.update(data, 0, data.length);
            byte[] out = new byte[64];
            hMac.doFinal(out, 0);
            return out;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static byte[] hash160(byte data[]) {
        return ripemd160(sha256(data));
    }

    public static byte[] ripemd160(byte data[])
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("RIPEMD160", "BC");
            byte[] hash = digest.digest(data);
            return hash;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    // returns a hash of any string 'A-z|0-1'
    // will not have any collisions
    // finds the index of a given string in
    // n-dimensional space.
    public static byte[] enHash(byte msg[]) {
        final byte alphabet[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_".getBytes();
        final int blockSize = 5;
        final int singleDim = alphabet.length;
        final int numBlocks = (msg.length / blockSize + 1) * blockSize;
        final BigInteger secondDim = new BigInteger(Integer.toString(alphabet.length)).pow(numBlocks);
        final int paddedLen = numBlocks * blockSize;

        final byte padded[] = Arrays.copyOf(msg, paddedLen);
        final int blocks[]  = new int[numBlocks];

        for (int i = 0; i < padded.length; i += blockSize) {
            // prepare the block
            int indexOf0 = Arrays.binarySearch(alphabet, padded[i]);
            int indexOf1 = Arrays.binarySearch(alphabet, padded[i + 1]);
            int indexOf2 = Arrays.binarySearch(alphabet, padded[i + 2]);
            int indexOf3 = Arrays.binarySearch(alphabet, padded[i + 3]);
            int indexOf4 = Arrays.binarySearch(alphabet, padded[i + 4]);

            // array = a0 b0 c0 d0 e0 a1 b1 c1 d1 e1
            // index = 0 + len * 0 1 1 0
            // x + WIDTH * (y + DEPTH * z)

            blocks[i / numBlocks] =
                    singleDim*singleDim*singleDim*singleDim * indexOf0
                  + singleDim*singleDim*singleDim * indexOf1
                  + singleDim*singleDim * indexOf2
                  + singleDim * indexOf3
                  + indexOf4
                    ;
        }

        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < numBlocks; i ++) {
            result = result.add(secondDim.pow(numBlocks - (i + 1)).multiply(new BigInteger(1, Utils.takeApart(blocks[i]))));
        }

        byte hash[] = result.toByteArray();
        if (hash[0] == 0) {
            byte temp[] = new byte[hash.length - 1];
            System.arraycopy(hash, 1, temp, 0, temp.length);
            hash = temp;
        }

        return hash;
    }
}
