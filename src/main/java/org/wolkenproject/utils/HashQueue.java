package org.wolkenproject.utils;

import org.wolkenproject.serialization.SerializableI;

import java.util.Queue;

public abstract class HashQueue<T extends SerializableI & Comparable<T>> implements Queue<T> {
}
