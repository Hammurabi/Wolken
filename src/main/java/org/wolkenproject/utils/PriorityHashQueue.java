package org.wolkenproject.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
public class PriorityHashQueue<T> implements Queue<T> {
    private Map<byte[], PriorityHashQueue.Entry<T>> entryMap;
}
