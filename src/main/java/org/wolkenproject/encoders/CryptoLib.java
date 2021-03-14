package org.wolkenproject.encoders;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.wolkenproject.exceptions.WolkenException;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class CryptoLib {
    private static final CryptoLib secureLib = new CryptoLib();
    static X9ECParameters params;
    static ECDomainParameters CURVE;

    public static CryptoLib getInstance() {
        return secureLib;
    }

    protected CryptoLib() {
        Security.addProvider(new BouncyCastleProvider());
        params = CustomNamedCurves.getByName("secp256k1");//SECNamedCurves.getByName("secp256k1");
        CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
    }

    /**
     * @param s The secret 32 bytes to initialize the private key.
     * @param curveName The curvename to use with key creation (secp256k1 by default).
     * @return A PrivateKey object.
     */
    public final PrivateKey genPrivateKey(BigInteger s, String curveName) throws WolkenException {
        try {
            ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(curveName);

            ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(s, ecParameterSpec);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            BCECPrivateKey key = (BCECPrivateKey) keyFactory.generatePrivate(privateKeySpec);

            if (key.getD().bitLength() > CURVE.getN().bitLength()) {
                while (key.getD().bitLength() > CURVE.getN().bitLength()) {
                    key = (BCECPrivateKey) genPrivateKey(key.getD().mod(CURVE.getN()), curveName);
                }

                if (key.getD().equals(BigInteger.ZERO))
                    throw new WolkenException("Resulting private key is ZERO.");

                throw new WolkenException("P%N==0.");
            }

            if (key.getD().equals(BigInteger.ZERO))
                throw new WolkenException("Resulting private key is ZERO.");

            return key;
        } catch (WolkenException e) {
            throw e;
        } catch (NoSuchAlgorithmException e) {
            throw new WolkenException(e.getMessage());
        } catch (InvalidKeySpecException e) {
            throw new WolkenException(e.getMessage());
        }
    }

    public final PublicKey generatePublicKey(ECPoint pointUncompressed, String curveName)
    {
        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(curveName);

        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(pointUncompressed, ecParameterSpec);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final ECPoint ec_point_point2oct(byte data[], boolean compressed)
    {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ECDomainParameters CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());

        ECPoint point = CURVE.getCurve().decodePoint(data);

        return point;
    }

    public BCECPublicKey derivePublicKey(BCECPrivateKey key) throws WolkenException {
        if (key == null) throw new WolkenException("cannot derivePublicKey public key from null private-key.");

        ECPoint point = new FixedPointCombMultiplier().multiply(CURVE.getG(), key.getD());

        return (BCECPublicKey) generatePublicKey(ec_point_point2oct(point.getEncoded(false), false), "secp256k1");
    }

    /**
     * @param priv Private key to use for signing data.
     * @param data A double sha256 of the data to be signed.
     * @return A Valid signature byte array.
     */
    public final byte[] sign(BCECPrivateKey priv, byte data[]) throws WolkenException {
        try {
            java.security.Signature dsa = java.security.Signature.getInstance("SHA256withECDSA", "BC");

            dsa.initSign(priv);

            dsa.update(data);

            return dsa.sign();
        } catch ( Throwable throwable ) {
            throw new WolkenException(throwable);
        }
    }

    public Signature sign(BCECPrivateKey privateKey, BCECPublicKey publicKey, byte data[]) throws WolkenException {
        ECDSASignature
        return new Signature(header, r, s);
    }

    /**
     * @param pubkey Public key to be used for signature verification.
     * @param data A sha256d of the original signed data.
     * @param sig The signature to verify.
     * @return True only if the Public key is derived from the signing private key.
     */
    public final boolean verifySignature(BCECPublicKey pubkey, byte[] data, byte[] sig) throws WolkenException {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(pubkey);

            signature.update(data);

            return signature.verify(sig);
        } catch (Throwable throwable) {
            throw new WolkenException(throwable);
        }
    }
}
