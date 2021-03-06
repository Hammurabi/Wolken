package org.wolkenproject.core.script;

public class OpcodeRegister {
    public void registerOp(String name, String desc, Opcode opcode) {
        addOp(name, null, desc, opcode);
    }

    // register an opcode into the vm
    public OpcodeRegister addOp(String name, BitFields args, String desc, Opcode opcode) {
        
    }
}
