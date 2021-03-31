package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.WolkenException;

public interface PapayaCallable {
    public static final PapayaCallable Default = scope -> {};
    public void call(Scope scope) throws WolkenException;
}
