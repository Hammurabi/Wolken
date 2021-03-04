package org.wolkenproject.utils;

public class Tuple<A, B> {
    private final A a;
    private final B b;

    public Tuple(A a, B b)
    {
        this.a = a;
        this.b = b;
    }

    public A getFirst()
    {
        return a;
    }

    public B getSecond()
    {
        return b;
    }
}
