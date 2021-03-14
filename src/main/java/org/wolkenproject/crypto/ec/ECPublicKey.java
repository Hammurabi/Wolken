package org.wolkenproject.crypto.ec;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.wolkenproject.crypto.CryptoLib;
import org.wolkenproject.crypto.Key;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ECPublicKey extends Key {
    private final byte key[];

    public ECPublicKey(byte key[]) {
        this.key = key;
    }

    @Override
    public BigInteger getKey() {
        return new BigInteger(1, key);
    }

    @Override
    public byte[] getRaw() {
        return Utils.trim(key, 1, key.length - 1);
    }

    @Override
    public byte[] getEncoded() {
        return key;
    }

    @Override
    public Key getCompressed() throws WolkenException {
        if (key[0] == 0x04) {
            ECPoint point = CryptoLib.getCurve().getCurve().decodePoint(key);
            ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
            ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(point, ecParameterSpec);

            KeyFactory keyFactory = null;
            try {
                keyFactory = KeyFactory.getInstance("EC");
            } catch (NoSuchAlgorithmException e) {
                throw new WolkenException(e);
            }

            try {
                return new ECPublicKey(((BCECPublicKey) keyFactory.generatePublic(publicKeySpec)).getQ().getEncoded(true));
            } catch (InvalidKeySpecException e) {
                throw new WolkenException(e);
            }
        }

        return this;
    }

    @Override
    public Key getDecompressed() throws WolkenException {
        if (key[0] == 0x04) {
            return this;
        }

        ECPoint point = CryptoLib.getCurve().getCurve().decodePoint(key);
        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(point, ecParameterSpec);

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new WolkenException(e);
        }

        try {
            return new ECPublicKey(((BCECPublicKey) keyFactory.generatePublic(publicKeySpec)).getQ().getEncoded(false));
        } catch (InvalidKeySpecException e) {
            throw new WolkenException(e);
        }
    }
}
