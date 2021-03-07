package org.wolkenproject.core.script;

import java.util.Set;

public class MochaVM extends VirtualMachine {
    @Override
    public MemoryState executeScript(Script script) {
        return getMemoryState();
    }
}
