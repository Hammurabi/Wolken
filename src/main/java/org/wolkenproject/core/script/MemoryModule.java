package org.wolkenproject.core.script;

public class MemoryModule {
    private MemoryState memoryState;

    public void setState(MemoryState memoryState) {
        this.memoryState = memoryState;
    }

    public MemoryState getState() {
        return memoryState;
    }
}
