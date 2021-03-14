package org.wolkenproject.crypto;

import org.wolkenproject.exceptions.WolkenException;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

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

    public abstract Signature sign(byte message[]) throws WolkenException;

    public static Keypair ellipticCurvePair() {
        return ellipticCurvePair(new SecureRandom());
    }

    public static Keypair ellipticCurvePair(Random random) {
        byte pkBytes[] = new byte[32];
        random.nextBytes(pkBytes);

        return ellipticCurvePair(new BigInteger(1, pkBytes));
    }
}
