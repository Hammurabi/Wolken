package org.wolkenproject.utils;

import java.util.*;

public class LinkedHashQueue<T extends Comparable<T>> implements HashQueue<T> {
    private Map<byte[], Entry<T>>   entryMap;
    private Queue<Entry<T>>         queue;
    private long                    byteCount;

    public LinkedHashQueue() {
        queue       = new LinkedList<>();
        entryMap    = new HashMap<>();
    }

    @Override
    public boolean containsKey(byte[] hash) {
        return entryMap.containsKey(hash);
    }

    @Override
    public void removeTails(int newLength) {
        while (!isEmpty() && size() > newLength) {
            poll();
        }
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

    private static class Entry<T> {
        private T       element;
        private byte[]  hash;
    }
}
