package org.wolkenproject.core.script;

import java.util.Stack;

public class MemoryModule {
    private Stack<MochaObject>  stack;

    public MemoryModule() {
    }

    public Stack<MochaObject> getStack() {
        return stack;
    }
}
