package org.wolkenproject.exceptions;

public class UndefFunctionException extends MochaException {
    public UndefFunctionException(String msg) {
        super(msg);
    }

    public UndefFunctionException(Throwable msg) {
        super(msg);
    }
}
