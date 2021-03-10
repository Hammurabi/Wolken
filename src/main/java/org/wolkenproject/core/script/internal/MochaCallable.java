package org.wolkenproject.core.script.internal;

import org.wolkenproject.core.script.VirtualProcess;

public interface MochaCallable {
    MochaObject execute(VirtualProcess process);
}
