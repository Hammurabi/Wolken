package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    public abstract ExecutionResult execute(Script script);
    public abstract void addOp(String name, int lenArgs, Opcode opcode);
}
