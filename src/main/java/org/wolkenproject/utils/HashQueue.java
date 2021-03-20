package org.wolkenproject.utils;

import org.wolkenproject.serialization.SerializableI;

import java.util.Queue;

public interface HashQueue<T extends SerializableI & Comparable<T>> implements Queue<T> {
}
