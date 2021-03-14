package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    private OpcodeRegister      opcodeRegister;
    public OpcodeRegister getOpcodeRegister() {
        return opcodeRegister;
    }
}
