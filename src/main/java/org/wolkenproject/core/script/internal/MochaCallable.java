package org.wolkenproject.core.script.internal;

import org.wolkenproject.core.script.Scope;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;

public interface MochaCallable {
    void call(Scope scope) throws MochaException, InvalidTransactionException;
}
