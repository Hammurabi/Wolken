package org.wolkenproject.core.script;

import org.wolkenproject.serialization.SerializableI;

public abstract class Opcode extends SerializableI {
    private int identifier;

    public void setIdentifier(int id) {
        this.identifier = id;
    }
}
