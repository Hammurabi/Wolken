package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.MochaException;

public interface MochaFunction {
    public MochaObject execute(VirtualProcess virtualProcess) throws MochaException;
}
