package org.wolkenproject.core.script;

public class VirtualProcess {
    private ClassProvider       classProvider;
    private VirtualMachine      virtualMachine;

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public ClassProvider getClassProvider() {
        return classProvider;
    }
}
