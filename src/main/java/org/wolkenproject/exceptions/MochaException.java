package org.wolkenproject.exceptions;

public class MochaException extends Exception {
    private static final long serialVersionUID = 5112431926980310096L;

    public MochaException(String msg)
    {
        super(msg);
    }
    public MochaException(Throwable cause)
    {
        super(cause);
    }
}
