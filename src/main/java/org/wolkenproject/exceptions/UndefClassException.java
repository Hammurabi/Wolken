package org.wolkenproject.exceptions;

public class UndefClassException extends Exception {
    public UndefClassException(String msg) {
        super(msg);
    }

    public UndefClassException(Throwable msg) {
        super(msg);
    }
}
