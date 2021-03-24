package org.wolkenproject.crypto.ec;

import org.wolkenproject.crypto.Key;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;

public class ECPrivateKey extends Key {
    private final byte key[];

    public ECPrivateKey(byte key[]) {
        this.key = key;
    }

    public static Key create() {
        return null;
    }

    @Override
    public BigInteger asInteger() {
        return new BigInteger(1, key);
    }

    @Override
    public byte[] getRaw() {
        return Utils.trim(key, 1, key.length - 1);
    }

    @Override
    public byte[] getEncoded() {
        return key;
    }

    @Override
    public Key getCompressed() throws WolkenException {
        return this;
    }

    @Override
    public Key getDecompressed() throws WolkenException {
        return this;
    }
}
