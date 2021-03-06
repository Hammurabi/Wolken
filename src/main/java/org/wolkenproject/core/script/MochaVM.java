package org.wolkenproject.core.script;

import java.util.Set;

public class MochaVM extends OpcodeRegister {
    private Set<Opcode> opcodeSet;

    @Override
    public ExecutionResult execute(Script script) {
        return null;
    }

    @Override
    public void registerOp(String name, BitFields args, String desc, Opcode opcode) {
        opcode.setIdentifier(opcodeSet.size());
        opcode.setArgs(args);
        opcode.setDescription(desc);
        opcodeSet.add(opcode);
    }
}
