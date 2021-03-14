package org.wolkenproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.exceptions.WolkenException;

import java.security.SecureRandom;

public class EcLib {
    @Test
    void recoverKeyFromSignature() throws WolkenException {
        SecureRandom random = new SecureRandom();
        byte message[] = new byte[256];

        for (int i = 0; i < 2000; i ++) {
            random.nextBytes(message);

            // make a random keypair
            Keypair keypair = Keypair.ellipticCurvePair();
            // sign
            Signature signature = keypair.sign(message);
            // verify signature
            Assertions.assertTrue(signature.checkSignature(message, keypair.getPublicKey()), "could not verify signature");
            // verify key
            Assertions.assertEquals(0, signature.recover(message).asInteger().compareTo(keypair.getPublicKey().asInteger()), "could not recover public key from signature");
        }
    }
}
