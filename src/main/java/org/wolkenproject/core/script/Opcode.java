package org.wolkenproject.core.script;

import org.wolkenproject.serialization.SerializableI;

public abstract class Opcode extends SerializableI {
    private int identifier;
    private BitFields args;

    public void setIdentifier(int id) {
        this.identifier = id;
    }

    public void setArgs(BitFields args) {
        this.args = args;
    }
}
