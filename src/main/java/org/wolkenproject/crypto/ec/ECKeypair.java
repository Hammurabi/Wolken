package org.wolkenproject.crypto.ec;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.wolkenproject.crypto.CryptoLib;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.Signature;

import java.math.BigInteger;

public class ECKeypair extends Keypair {
    public ECKeypair(BigInteger priv, BigInteger pubk) {
        super(priv, pubk);
    }

    public ECSig ellipticCurveSign(byte message[]) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(getPrivateKey(), CryptoLib.CURVE);
        signer.init(true, privateKeyParameters);

        BigInteger components[] = signer.generateSignature(message);

        return new ECSig(components[0], components[1]).toCanonicalised();
    }

    @Override
    public Signature sign(byte[] message) {
        return null;
    }
}
