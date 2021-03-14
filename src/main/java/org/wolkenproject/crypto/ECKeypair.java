package org.wolkenproject.crypto;

import java.math.BigInteger;

public class ECKeypair extends Keypair {
    public ECKeypair(BigInteger priv, BigInteger pubk) {
        super(priv, pubk);
    }

    @Override
    public Signature sign(byte[] message) {
        return null;
    }
}
