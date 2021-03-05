package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    public abstract ExecutionResult execute(Script script);
    public void addOp(String name, String desc, Opcode opcode) {
        addOp(name, null, desc, opcode);
    }
    public abstract void addOp(String name, BitFields args, String desc, Opcode opcode);
}
