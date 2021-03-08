package org.wolkenproject.core.script;

import java.util.LinkedHashSet;
import java.util.Set;

public class OpcodeRegister {
    private Set<Opcode> opcodeList;

    public OpcodeRegister() {
        opcodeList = new LinkedHashSet<>();
    }

    // register an opcode into the vm
    public OpcodeRegister registerOp(Opcode opcode) {
        opcode.setIdentifier(opcodeList.size());
        opcodeList.add(opcode);

        return this;
    }

    public Opcode getOpcode(int opcode) {
        for (Opcode op : opcodeList) {
            if (op.getIdentifier() == opcode) {
                return op;
            }
        }

        return null;
    }

    public int opCount() {
        return opcodeList.size();
    }
}
