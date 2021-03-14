package org.wolkenproject.crypto.ec;

import org.wolkenproject.crypto.Key;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;

public class EcPublicKey extends Key {
    private final byte key[];

    public EcPublicKey(byte key[]) {
        this.key = key;
    }

    @Override
    public BigInteger getKey() {
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
    public Key getCompressed() {
        return null;
    }
}
