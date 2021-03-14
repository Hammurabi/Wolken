package org.wolkenproject.core.script.internal;

public class MochaECSig extends MochaObject {
    private byte signature[];

    public MochaECSig(byte signature[]) {
        this.signature = signature;
    }

    public byte[] getSignature() {
        return signature;
    }
}
