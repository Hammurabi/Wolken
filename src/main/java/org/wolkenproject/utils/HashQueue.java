package org.wolkenproject.utils;

public interface HashQueue<T> {
    boolean containsKey(byte[] hash);
    void removeTails(int newLength);
    T getByHash(byte[] hash);
    void add(T element, byte hash[]);
    T poll();
    T peek();
    int size();
    // should return the amount of bytes in memory this queue is taking (if available)
    long byteCount();
    default boolean hasElements() {
        return !isEmpty();
    }
    default boolean isEmpty() { return size() != 0; }
}
