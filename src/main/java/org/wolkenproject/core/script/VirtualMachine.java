package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    private OpcodeRegister      opcodeRegister;
    private ClassProvider       metadataProvider;
    private MemoryModule        memoryModule;

    public abstract void executeScript(Script script);

    public OpcodeRegister getOpcodeRegister() {
        return opcodeRegister;
    }

    public void swapMemoryState(MemoryState memoryState) {
        memoryModule.setState(memoryState);
    }

    public MemoryState getMemoryState() {
        return memoryModule.getState();
    }

    public ClassProvider getClassProvider() {
        return metadataProvider;
    }
}
