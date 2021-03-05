package org.wolkenproject.utils;

public interface Hashable {
    byte[] getHash160();
    byte[] getHash256();
    byte[] getHash256d();
}
