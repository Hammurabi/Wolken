package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    private OpcodeRegister      opcodeRegister;

    public abstract VirtualProcess createProcess(Script script);

    public OpcodeRegister getOpcodeRegister() {
        return opcodeRegister;
    }
}
