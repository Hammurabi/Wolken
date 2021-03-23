package org.wolkenproject.utils;

import java.util.Queue;

public interface HashQueue<T> extends Queue<T> {
    boolean containsKey(byte[] hash);
    void removeTails(int newLength);
    T getByHash(byte[] hash);
}
