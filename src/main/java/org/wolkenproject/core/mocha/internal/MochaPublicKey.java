package org.wolkenproject.core.mocha.internal;

import org.wolkenproject.core.mocha.MochaBool;
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
