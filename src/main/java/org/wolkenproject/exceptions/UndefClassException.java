package org.wolkenproject.exceptions;

public class UndefClassException extends WolkenException {
    public UndefClassException(String msg) {
        super(msg);
    }

    public UndefClassException(Throwable msg) {
        super(msg);
    }
}
