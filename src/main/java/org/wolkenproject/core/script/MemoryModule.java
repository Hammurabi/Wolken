package org.wolkenproject.core.script;

public class MemoryModule {
    private MemoryState memoryState;
    private Register    registers[];

    public MemoryModule() {
    }

    public Register getRegister(int registerID) {
    }

    public void setState(MemoryState memoryState) {
        this.memoryState = memoryState;
    }

    public MemoryState getState() {
        return memoryState;
    }

    protected class Register {
    }
}
