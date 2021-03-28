package org.wolkenproject.core.papaya;

import org.wolkenproject.exceptions.WolkenException;

public interface PapayaCallable {
    public void call(Scope scope) throws WolkenException;
}
