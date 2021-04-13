package org.wolkenproject.utils;

import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.util.*;

public class PriorityHashQueue<T extends Comparable<T>> implements HashQueue<T> {
    private Map<byte[], Entry<T>>   entryMap;
    private Queue<Entry<T>>         queue;
    private Comparator<Entry<T>>    comparator;

    public PriorityHashQueue(Comparator<Entry<T>> comparator) {
        queue       = new PriorityQueue<>(comparator);
        entryMap    = new HashMap<>();
        this.comparator = comparator;
    }

    private static class DefaultComparator<T extends Comparable<T>> implements Comparator<Entry<T>> {
        @Override
        public int compare(Entry<T> a, Entry<T> b) {
            return a.compareTo(b);
        }
    }

    private static class Entry<T extends Comparable<T>> implements Comparable<Entry<T>> {
        private T       element;
        private byte[]  hash;

        @Override
        public int compareTo(Entry<T> b) {
            return element.compareTo(b.element);
        }
    }
}
