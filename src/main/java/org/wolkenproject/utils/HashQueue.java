package org.wolkenproject.utils;

public interface HashQueue<T> {
    boolean containsKey(byte[] hash);
    void removeTails(int newLength);
    T getByHash(byte[] hash);
    void add(T element, byte hash[]);
    T poll();
    T peek();
    void remove(byte hash[]);
    int size();
    // should return the amount of bytes in memory this queue is taking (if available)
    int byteCount();
    default boolean hasElements() {
        return !isEmpty();
    }
    boolean isEmpty();
}
