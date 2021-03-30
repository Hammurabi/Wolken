package org.wolkenproject.exceptions;

public class PapayaIllegalAccessException extends PapayaException {
    public PapayaIllegalAccessException() {
        this("");
    }

    public PapayaIllegalAccessException(String msg) {
        super(msg);
    }
}
