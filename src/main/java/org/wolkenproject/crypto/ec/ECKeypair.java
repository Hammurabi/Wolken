package org.wolkenproject.crypto.ec;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.wolkenproject.crypto.CryptoLib;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;

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
    public Signature sign(byte[] message) throws WolkenException {
        BigInteger publicKey = getPublicKey();
        // hash with sha256d
        byte[] messageHash = HashUtil.sha256d(message);

        ECSig sig = ellipticCurveSign(messageHash);

        // Now we have to work backwards to figure out the recId needed to recover the signature.
        int recId = -1;
        for (int i = 0; i < 4; i++) {
            BigInteger k = CryptoLib.recoverFromSignature(i, sig, messageHash);
            if (k != null && k.equals(publicKey)) {
                recId = i;
                break;
            }
        }

        if (recId == -1) {
            throw new WolkenException("Could not construct a recoverable key.");
        }

        int headerByte = recId + 27;

        // 1 header + 32 bytes for R + 32 bytes for S
        byte[] r = toBytesPadded(sig.getR(), 32);
        byte[] s = toBytesPadded(sig.getS(), 32);

        return new RecoverableSignature((byte) headerByte, r, s);
    }

    private byte[] toBytesPadded(BigInteger integer, int length) throws WolkenException {
        byte result[] = Utils.toBytesPadded(integer, length);

        if (result.length > length) {
            throw new WolkenException("result is too big.");
        }

        return result;
    }
}
