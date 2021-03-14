package org.wolkenproject.crypto;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

import java.math.BigInteger;

public abstract class Keypair {
    private final BigInteger privateKey;
    private final BigInteger publicKey;

    public Keypair(BigInteger priv, BigInteger pubk) {
        this.privateKey = priv;
        this.publicKey  = pubk;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public abstract Signature sign(byte message[]);
}
