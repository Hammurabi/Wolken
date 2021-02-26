package org.wokenproject.exceptions;

public class InvalidSerialNumberException extends WolkenException {
    private static final long serialVersionUID = 2847962769776745698L;

    public InvalidSerialNumberException(String msg) {
        super(msg);
    }

    public InvalidSerialNumberException(Throwable throwable)
    {
        super(throwable);
    }
}
