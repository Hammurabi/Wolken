package org.wolkenproject.core.script.internal;

import org.wolkenproject.core.script.Scope;

public interface MochaCallable {
    MochaObject call(Scope scope);
}
