package org.wolkenproject.core.script;

public class VirtualProcess {
    private ClassProvider       classProvider;
    private VirtualMachine      virtualMachine;
    private MemoryModule        memoryModule;

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public ClassProvider getClassProvider() {
        return classProvider;
    }

    public void swapMemoryState(MemoryState memoryState) {
        memoryModule.setState(memoryState);
    }

    public MemoryState getMemoryState() {
        return memoryModule.getState();
    }
}
