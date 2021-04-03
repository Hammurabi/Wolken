package org.wolkenproject.utils;

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
    public static long enHash(byte msg[]) {
        final byte alphabet[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_".getBytes();
        final int blockSize = 5;
        final int numBlocks = (msg.length / blockSize + 1) * blockSize;
        final int paddedLen = numBlocks * blockSize;

        final byte padded[] = Arrays.copyOf(msg, paddedLen);
        final int blocks[]  = new int[numBlocks];

        for (int i = 0; i < padded.length; i += blockSize) {
        }
    }
}
