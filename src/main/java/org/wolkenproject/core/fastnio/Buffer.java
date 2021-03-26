package org.wolkenproject.core.fastnio;

import jdk.internal.misc.Unsafe;

import java.lang.reflect.Field;

public abstract class Buffer {
    static final Unsafe UnsafeInstance = getUnsafe();

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
}
