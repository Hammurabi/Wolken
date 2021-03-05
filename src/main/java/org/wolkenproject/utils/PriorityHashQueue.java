package org.wolkenproject.utils;

import java.util.*;

public class PriorityHashQueue<T extends Comparable<T>> implements Queue<T> {
    private Map<byte[], Entry<T>>   entryMap;
    private Queue<Entry<T>>         queue;

    private static class DefaultComparator<T extends Comparable<T>> implements Comparator<Entry<T>> {
        @Override
        public int compare(Entry<T> tEntry, Entry<T> t1) {
            return 0;
        }
    }

    private static class Entry<T extends Comparable<T>> implements Comparable<Entry<T>> {
        private T element;

        @Override
        public int compareTo(Entry<T> b) {
            return element.compareTo(b.element);
        }
    }
}
