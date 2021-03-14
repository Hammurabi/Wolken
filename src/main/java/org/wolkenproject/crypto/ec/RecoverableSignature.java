package org.wolkenproject.crypto.ec;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.math.ec.ECPoint;
import org.wolkenproject.core.Context;
import org.wolkenproject.crypto.CryptoLib;
import org.wolkenproject.crypto.Key;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Assertions;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class RecoverableSignature extends Signature {
    private final byte v;
    private final byte r[];
    private final byte s[];

    public RecoverableSignature() {
        this((byte) 0, new byte[32], new byte[32]);
    }

    public RecoverableSignature(byte v, byte[] r, byte[] s) {
        this.v = v;
        this.r = r;
        this.s = s;
    }

    public byte getV() {
        return v;
    }

    public byte[] getR() {
        return r;
    }

    public byte[] getS() {
        return s;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RecoverableSignature();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(RecoverableSignature.class);
    }

    @Override
    public boolean checkSignature(byte[] originalMessage, Key publicKey) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ECDomainParameters curve = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());

        byte pubK[] = publicKey.toByteArray();
        System.out.println(pubK.length + " " + pubK[0]);
        ECPoint point = curve.getCurve().decodePoint(publicKey.toByteArray());

        signer.init(false, new ECPublicKeyParameters(point, CryptoLib.getCurve()));
        return signer.verifySignature(HashUtil.sha256d(originalMessage), new BigInteger(1, r), new BigInteger(1, s));
    }

    @Override
    public Key recover(byte originalMessage[]) throws WolkenException {
        Assertions.assertTrue(r != null && r.length == 32, "r must be 32 bytes in length");
        Assertions.assertTrue(s != null && s.length == 32, "s must be 32 bytes in length");

        int header = v & 0xFF;

        if (header < 27 || header > 34) {
            throw new WolkenException("header byte out of range: " + header);
        }

        ECSig sig = new ECSig(new BigInteger(1, r), new BigInteger(1, s));
        BigInteger result = CryptoLib.recoverFromSignature(v - 27, sig, HashUtil.sha256d(originalMessage));

        if (result == null) {
            throw new WolkenException("could not recover public key.");
        }

        return result;
    }
}