package org.wolkenproject.utils;

public abstract class Hashable {
    abstract byte[] getHash160();
    abstract byte[] getHash256();
    abstract byte[] getHash256d();
}
