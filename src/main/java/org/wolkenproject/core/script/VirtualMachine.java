package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    private OpcodeRegister  opcodeRegister;
    private MemoryState     memoryState;
    private MemoryModule    memoryModule;

    public abstract MemoryState executeScript(Script script);

    public OpcodeRegister getOpcodeRegister() {
        return opcodeRegister;
    }

    public void swapMemoryState(MemoryState memoryState) {
        this.memoryState = memoryState;
    }

    public MemoryState getMemoryState() {
        return memoryState;
    }
}
