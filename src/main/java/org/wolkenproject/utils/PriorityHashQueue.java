package org.wolkenproject.utils;

import java.util.*;

public class PriorityHashQueue<T extends Comparable<T>> implements Queue<T> {
    private Map<byte[], Entry<T>>   entryMap;
    private Queue<Entry<T>>         queue;

    private static class DefaultComparator implements Comparator<Entry<T extends Comparable<T>>> {
    }

    private static class Entry<T extends Comparable<T>> implements Comparable<Entry<T>> {
        private T element;

        @Override
        public int compareTo(Entry<T> b) {
            return element.compareTo(b.element);
        }
    }
}
