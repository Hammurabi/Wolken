package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    public abstract void executeScript(Script script);
    public OpcodeRegister getOpcodeRegister() {
        
    }
}
