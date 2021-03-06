package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    private OpcodeRegister opcodeRegister;

    public abstract void executeScript(Script script);
    public OpcodeRegister getOpcodeRegister() {
        return opcodeRegister;
    }
}
