package org.wolkenproject.utils;

import java.util.*;

public class PriorityHashQueue<T> implements Queue<T> {
    private Map<byte[], Entry<T>>   entryMap;
    private Queue<Entry<T>>         queue;

    private static class Entry<T> implements Comparable<Entry<T>> {
        private T element;

        @Override
        public int compareTo(Entry<T> b) {
            return element.compareTo(b);
        }
    }
}
