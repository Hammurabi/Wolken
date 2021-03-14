package org.wolkenproject.exceptions;

public class UndefClassException extends MochaException {
    public UndefClassException(String msg) {
        super(msg);
    }

    public UndefClassException(Throwable msg) {
        super(msg);
    }
}
