package org.wolkenproject.core.script;

import org.wolkenproject.serialization.SerializableI;

public abstract class Opcode extends SerializableI {
    private int identifier;
    private BitFields args;
    private String    desc;

    protected void setIdentifier(int id) {
        this.identifier = id;
    }

    protected void setArgs(BitFields args) {
        this.args = args;
    }

    protected void setDescription(String desc) {
        this.desc = desc;
    }

    // return false for halt type opcodes.
    public abstract boolean execute(VirtualProcess virtualProcess);

    public int getIdentifier() {
        return identifier;
    }

    public BitFields getArgs() {
        return args;
    }

    public String getDesc() {
        return desc;
    }
}
