package org.wolkenproject.core.mocha.internal;

import org.wolkenproject.core.mocha.Scope;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;

public interface MochaCallable {
    MochaObject call(Scope scope) throws MochaException, InvalidTransactionException;
}
