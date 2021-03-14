package org.wolkenproject.crypto;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.wolkenproject.encoders.CryptoLib;
import org.wolkenproject.encoders.ECSig;

import java.math.BigInteger;

public class Keypair {
    private final BigInteger privateKey;
    private final BigInteger publicKey;

    public Keypair(BigInteger priv, BigInteger pubk) {
        this.privateKey = priv;
        this.publicKey  = pubk;
    }

    public ECSig sign(byte message[]) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(getPrivateKey(), CryptoLib.CURVE);
        signer.init(true, privateKeyParameters);

        BigInteger components[] = signer.generateSignature(message);

        return new ECSig(components[0], components[1]).toCanonicalised();;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }
}
