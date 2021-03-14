package org.wolkenproject.crypto;

public abstract class Key {
    // should return only the significant key bytes
    public abstract byte[] getRaw();
    // should return a neatly packed version of this key
    public abstract byte[] getEncoded();
    // should return a compressed version of this key
    public abstract Key getCompressed();
}
