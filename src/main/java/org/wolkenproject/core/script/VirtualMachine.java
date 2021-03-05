package org.wolkenproject.core.script;

public abstract class VirtualMachine {
    public abstract ExecutionResult execute(Script script);
    public void addOp(String name, boolean hasArgs, int lenArgs, int maxLenArgs, Opcode opcode) {
        addOp(name, hasArgs, lenArgs, maxLenArgs, "", opcode);
    }

    public abstract void addOp(String name, boolean hasArgs, int lenArgs, int maxLenArgs, String desc, Opcode opcode);
}
