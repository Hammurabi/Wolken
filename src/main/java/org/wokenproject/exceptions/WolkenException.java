package org.wokenproject.exceptions;

public class WolkenException extends Exception {
    public WolkenException(String msg)
    {
        super(msg);
    }
    public WolkenException(Throwable cause)
    {
        super(cause);
    }
}
