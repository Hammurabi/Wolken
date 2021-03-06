package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    private OpcodeRegister  opcodeRegister;
    private MemoryModule    memoryModule;

    public abstract MemoryState executeScript(Script script);

    public OpcodeRegister getOpcodeRegister() {
        return opcodeRegister;
    }

    public void swapMemoryState(MemoryState memoryState) {
        memoryModule.setState(memoryState);
    }

    public MemoryState getMemoryState() {
        return memoryModule.getState();
    }
}
