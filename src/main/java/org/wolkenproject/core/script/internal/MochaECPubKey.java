package org.wolkenproject.core.script.internal;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.wolkenproject.core.script.MochaBool;
import org.wolkenproject.encoders.CryptoLib;
import org.wolkenproject.exceptions.WolkenException;

public class MochaECPubKey extends MochaObject {
    private BCECPublicKey   publicKey;
    private boolean         compressed;

    public MochaECPubKey(byte key[]) {
        this.publicKey = (BCECPublicKey) CryptoLib.getInstance().generatePublicKey(CryptoLib.ec_point_point2oct(key, key[0] == 0x3), "secp256k1");
        this.compressed= key[0] == 0x3;
    }

    public MochaObject verifySignature(MochaECSig signature, byte signatureData[]) {
        try {
            return new MochaBool(CryptoLib.getInstance().verifySignature(publicKey, signatureData, signature.getSignature()));
        } catch (WolkenException e) {
            return new MochaBool(false);
        }
    }

    public boolean isCompressed() {
        return compressed;
    }
}
