package org.wolkenproject.core.script.internal;

import org.wolkenproject.core.script.MochaBool;
import org.wolkenproject.crypto.Key;

public class MochaPublicKey extends MochaObject {
    private Key             publicKey;

    public MochaPublicKey(Key key) {
        this.publicKey = key;
    }

    public MochaObject checkSignature(MochaCryptoSignature signature, byte signatureData[]) {
        return new MochaBool(signature.getSignature().checkSignature(signatureData, publicKey));
    }
}
