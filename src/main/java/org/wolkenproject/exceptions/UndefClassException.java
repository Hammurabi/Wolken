package org.wolkenproject.exceptions;

public class UndefClassException extends PapayaException {
    public UndefClassException(String msg) {
        super(msg);
    }

    public UndefClassException(Throwable msg) {
        super(msg);
    }
}
