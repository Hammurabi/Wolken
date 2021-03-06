package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    public abstract OpcodeRegister getOpcodeRegister();
    public abstract void executeScript(Script script);
}
