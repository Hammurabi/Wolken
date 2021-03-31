package org.wolkenproject.papaya.internal;

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
