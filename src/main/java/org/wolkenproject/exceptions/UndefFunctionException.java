package org.wolkenproject.exceptions;

public class UndefFunctionException extends WolkenException {
    public UndefFunctionException(String msg) {
        super(msg);
    }

    public UndefFunctionException(Throwable msg) {
        super(msg);
    }
}
