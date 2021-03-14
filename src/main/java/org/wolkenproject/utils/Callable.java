package org.wolkenproject.utils;

public interface Callable<Return, Argument> {
    public Return call(Argument argument);
}
