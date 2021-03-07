package org.wolkenproject.core.script;

public class VirtualProcess {
    private ClassProvider       classProvider;
    private MemoryModule        memoryModule;
    private VirtualMachine      virtualMachine;

    public void stopProcess(int signal) {
    }

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
