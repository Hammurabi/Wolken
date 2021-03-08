package org.wolkenproject.exceptions;

public class InvalidOpcodeException extends Exception {
    public InvalidOpcodeException(String msg) {
        super(msg);
    }

    public InvalidOpcodeException(Throwable msg) {
        super(msg);
    }
}
