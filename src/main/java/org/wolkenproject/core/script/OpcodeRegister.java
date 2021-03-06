package org.wolkenproject.core.script;

import java.util.List;

public class OpcodeRegister {
    private List<Opcode> opcodeList;

    public void registerOp(String name, String desc, Opcode opcode) {
        registerOp(name, null, desc, opcode);
    }

    // register an opcode into the vm
    public OpcodeRegister registerOp(String name, BitFields args, String desc, Opcode opcode) {
        opcodeList.add(opcode);

        return this;
    }
}
