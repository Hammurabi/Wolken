package org.wolkenproject.core.fastnio;

import jdk.internal.misc.Unsafe;

import java.lang.reflect.Field;

public abstract class Buffer {
    static final Unsafe UnsafeInstance = getUnsafe();
    private int position;

    private static final Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return  (Unsafe) f.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Buffer createBuffer(int length) {
        return null;
    }

    public static Buffer createUnsafeBuffer(int length) {
        return null;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void flip() {
        setPosition(0);
    }

    public abstract void put(int offset, int value);
    public abstract void put(int offset, byte value[]);
    public abstract void putShort(int offset, int value);
    public abstract void putChar(int offset, int value);
    public abstract void putInt(int offset, int value);
    public abstract void putLong(int offset, long value);

    public abstract int get(int offset);
    public abstract void get(int offset, byte out[]);
    public abstract int getShort(int offset);
    public abstract int getChar(int offset);
    public abstract int getInt(int offset);
    public abstract long getLong(int offset);
}
