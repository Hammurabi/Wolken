package org.wolkenproject.core.fastnio;

import org.wolkenproject.utils.Utils;

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
        buffer[offset] = (byte) value;
    }

    @Override
    public void put(int offset, byte[] value) {
        for (int i = 0; i < value.length; i ++) {
            put(offset + i, value[i]);
        }
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
        return buffer[offset];
    }

    @Override
    public void get(int offset, byte[] out) {
        for (int i = 0; i < out.length; i ++) {
            out[i] = (byte) get(offset + i);
        }
    }

    @Override
    public int getShort(int offset) {
        return Utils.makeShort(buffer[offset], buffer[offset + 1]);
    }

    @Override
    public int getChar(int offset) {
        return Utils.makeChar(buffer[offset], buffer[offset + 1]);
    }

    @Override
    public int getInt(int offset) {
        return Utils.makeInt(buffer, offset);
    }

    @Override
    public long getLong(int offset) {
        return Utils.makeLong(buffer, offset);
    }
}
