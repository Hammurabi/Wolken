package org.wolkenproject.core.papaya;

import org.wolkenproject.exceptions.WolkenException;

public class PapayaObject {
    private PapayaCallable callable;
    // contains all sub-objects
    private PapayaObject children[];

    public void call(Scope scope) throws WolkenException {
        callable.call(scope);
    }
}
