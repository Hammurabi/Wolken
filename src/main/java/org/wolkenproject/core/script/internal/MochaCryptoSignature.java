package org.wolkenproject.core.script.internal;

import org.wolkenproject.crypto.Signature;

public class MochaCryptoSignature extends MochaObject {
    private Signature signature;

    public MochaCryptoSignature(Signature signature) {
        this.signature = signature;
    }

    public Signature getSignature() {
        return signature;
    }
}
