package org.wolkenproject.crypto;

import org.wolkenproject.exceptions.WolkenException;

import java.math.BigInteger;

public abstract class Key {
    // return the actual key as an integer
    public abstract BigInteger getKey();
    // should return only the significant key bytes
    public abstract byte[] getRaw();
    // should return a neatly packed version of this key
    public abstract byte[] getEncoded();
    // should return a compressed version of this key
    public abstract Key getCompressed() throws WolkenException;
}
