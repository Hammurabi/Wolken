package org.wolkenproject.utils;

import java.util.*;

public class PriorityHashQueue<T extends Comparable<T>> implements HashQueue<T> {
    private Map<byte[], Entry<T>>   entryMap;
    private Queue<Entry<T>>         queue;
    private Comparator<Entry<T>>    comparator;
    private long                    byteCount;

    public PriorityHashQueue() {
        this(new DefaultComparator<>());
    }

    public PriorityHashQueue(Comparator<Entry<T>> comparator) {
        queue       = new PriorityQueue<>(comparator);
        entryMap    = new HashMap<>();
        this.comparator = comparator;
    }

    @Override
    public boolean containsKey(byte[] hash) {
        return entryMap.containsKey(hash);
    }

    @Override
    public void removeTails(int newLength) {
        PriorityHashQueue<T> newQueue = new PriorityHashQueue<>(comparator);

        while (!isEmpty() && newQueue.size() < newLength) {
            Entry<T> e = queue.poll();
            newQueue.add(e.element, e.hash);
        }

        this.entryMap   = newQueue.entryMap;
        this.queue      = newQueue.queue;
        this.byteCount  = newQueue.byteCount;
    }

    @Override
    public T getByHash(byte[] hash) {
        if (entryMap.containsKey(hash)) {
            return entryMap.get(hash).element;
        }

        return null;
    }

    @Override
    public void add(T element, byte[] hash) {
        Entry<T> entry  = new Entry<>();
        entry.element   = element;
        entry.hash      = hash;

        entryMap.put(hash, entry);
        queue.add(entry);
    }

    @Override
    public T poll() {
        if (queue.isEmpty()) {
            return null;
        }

        Entry<T> entry = queue.poll();
        entryMap.remove(entry.hash);

        return entry.element;
    }

    @Override
    public T peek() {
        if (queue.isEmpty()) {
            return null;
        }

        return queue.peek().element;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public long byteCount() {
        return byteCount;
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
