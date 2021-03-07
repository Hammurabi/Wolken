package org.wolkenproject.core.script;

import java.util.LinkedHashSet;
import java.util.Set;

public class OpcodeRegister {
    private Set<Opcode> opcodeList;

    public OpcodeRegister() {
        opcodeList = new LinkedHashSet<>();
    }

    public void registerOp(String name, String desc, OpcodeExecutor executor) {
        registerOp(name, null, desc, executor);
    }

    // register an opcode into the vm
    public OpcodeRegister registerOp(String name, BitFields args, String desc, OpcodeExecutor executor) {
        Opcode opcode = new Opcode();
        opcode.setIdentifier(opcodeList.size());
        opcode.setArgs(args);
        opcode.setDescription(desc);
        opcode.setExecutor(executor);
        opcodeList.add(opcode);

        return this;
    }

    public int opCound() {
        return opcodeList.size();
    }
}
