package org.wolkenproject.core.script;

import java.util.LinkedHashSet;
import java.util.Set;

public class OpcodeRegister {
    private Set<Opcode> opcodeList;

    public OpcodeRegister() {
        opcodeList = new LinkedHashSet<>();
    }

    public void registerOp(String name, String desc, Opcode opcode) {
        registerOp(name, null, desc, opcode);
    }

    // register an opcode into the vm
    public OpcodeRegister registerOp(String name, BitFields args, String desc, Opcode opcode) {
        opcode.setIdentifier(opcodeList.size());
        opcode.setArgs(args);
        opcode.setDescription(desc);
        opcodeList.add(opcode);

        return this;
    }
}
