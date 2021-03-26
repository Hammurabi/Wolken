package org.wolkenproject.core.fastnio;

class HeapBuffer extends Buffer {
    private final byte[] buffer;

    HeapBuffer(int size) {
        this.buffer = new byte[size];
    }

    @Override
    public int getLength() {
        return buffer.length;
    }

    @Override
    public void put(int offset, int value) {
    }

    @Override
    public void put(int offset, byte[] value) {
    }

    @Override
    public void putShort(int offset, int value) {
    }

    @Override
    public void putChar(int offset, int value) {
    }

    @Override
    public void putInt(int offset, int value) {
    }

    @Override
    public void putLong(int offset, long value) {
    }

    @Override
    public int get(int offset) {
        return 0;
    }

    @Override
    public void get(int offset, byte[] out) {
    }

    @Override
    public int getShort(int offset) {
        return 0;
    }

    @Override
    public int getChar(int offset) {
        return 0;
    }

    @Override
    public int getInt(int offset) {
        return 0;
    }

    @Override
    public long getLong(int offset) {
        return 0;
    }
}
