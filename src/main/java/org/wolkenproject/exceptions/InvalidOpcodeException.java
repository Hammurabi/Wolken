package org.wolkenproject.exceptions;

public class InvalidOpcodeException extends MochaException {
    public InvalidOpcodeException(String msg) {
        super(msg);
    }

    public InvalidOpcodeException(Throwable msg) {
        super(msg);
    }
}
