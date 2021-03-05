package org.wolkenproject.utils;

import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.util.*;

public class PriorityHashQueue<T extends SerializableI & Comparable<T>> implements Queue<T> {
    private Map<byte[], Entry<T>>   entryMap;
    private Queue<Entry<T>>         queue;
    private Class<T>                tClass;

    public PriorityHashQueue(Class<T> tClass) {
        this(new DefaultComparator<>(), tClass);
    }

    public PriorityHashQueue(Comparator<Entry<T>> comparator, Class<T> tClass) {
        queue       = new PriorityQueue<Entry<T>>(comparator);
        entryMap    = new HashMap<>();
        this.tClass = tClass;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (tClass.isInstance(o)) {
            return entryMap.containsKey(((Hashable) o).getHash160());
        } else if (o instanceof byte[]) {
            return entryMap.containsKey(o);
        }

        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return null;
    }

    @Override
    public boolean add(T t) {
        Entry<T> entry = new Entry<>();
        entry.element   = t;
        try {
            entry.hash      = t.checksum();
        } catch (IOException e) {
            entry.hash      = t.asByteArray();
        }

        boolean contained   = entryMap.containsKey(entry.hash);

        if (!contained) {
            queue.add(entry);
            entryMap.put(entry.hash, entry);
        }

        return !contained;
    }

    @Override
    public boolean remove(Object o) {
        byte checksum[] = null;
        try {
            checksum        = ((T) o).checksum();
        } catch (IOException e) {
            checksum        = ((T) o).asByteArray();
        }

        boolean contained   = entryMap.containsKey(checksum);
        entryMap.remove(checksum);

        return contained;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public void clear() {
        entryMap.clear();
        queue.clear();
    }

    @Override
    public boolean offer(T t) {
        return false;
    }

    @Override
    public T remove() {
        return poll();
    }

    @Override
    public T poll() {
        Entry<T> entry = queue.poll();
        entryMap.remove(entry.hash);

        return entry.element;
    }

    @Override
    public T element() {
        Entry<T> entry = queue.poll();
        entryMap.remove(entry.hash);

        return entry.element;
    }

    @Override
    public T peek() {
        return queue.peek().element;
    }

    public T getByHash(byte[] txid) {
        if (entryMap.containsKey(txid)) {
            return entryMap.get(txid).element;
        }

        return null;
    }

    private static class DefaultComparator<T extends SerializableI & Comparable<T>> implements Comparator<Entry<T>> {
        @Override
        public int compare(Entry<T> a, Entry<T> b) {
            return a.compareTo(b);
        }
    }

    private static class Entry<T extends SerializableI & Comparable<T>> implements Comparable<Entry<T>> {
        private T       element;
        private byte[]  hash;

        @Override
        public int compareTo(Entry<T> b) {
            return element.compareTo(b.element);
        }
    }
}
