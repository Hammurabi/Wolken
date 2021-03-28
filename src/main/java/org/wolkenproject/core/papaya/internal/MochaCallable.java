package org.wolkenproject.core.papaya.internal;

import org.wolkenproject.core.papaya.Scope;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;

public interface MochaCallable {
    MochaObject call(Scope scope) throws MochaException, InvalidTransactionException;
}
