package org.wolkenproject.exceptions;

public class UndefMemberException extends WolkenException {
    public UndefMemberException(String msg) {
        super(msg);
    }

    public UndefMemberException(Throwable msg) {
        super(msg);
    }
}
