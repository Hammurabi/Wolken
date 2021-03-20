package org.wolkenproject.utils;

import org.wolkenproject.serialization.SerializableI;

import java.util.PriorityQueue;
import java.util.Queue;

public interface HashQueue<T extends SerializableI & Comparable<T>> extends Queue<T> {
    boolean containsKey(byte[] hash);
    void removeTails(int newLength);
}
