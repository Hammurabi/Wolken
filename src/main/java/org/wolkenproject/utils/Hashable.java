package org.wolkenproject.utils;

public abstract class Hashable {
    byte[] getHash160();
    byte[] getHash256();
    byte[] getHash256d();
}
