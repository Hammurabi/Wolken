package org.wolkenproject.exceptions;

public class InvalidMemoryAccess extends MochaException {
    private static final long serialVersionUID = -7127993326157797983L;

    public InvalidMemoryAccess(String msg) {
        super(msg);
    }

    public InvalidMemoryAccess(Throwable throwable)
    {
        super(throwable);
    }
}
