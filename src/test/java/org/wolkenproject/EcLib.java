package org.wolkenproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.security.SecureRandom;

public class EcLib {
    @Test
    void recoverKeyFromSignature() throws WolkenException {
        SecureRandom random = new SecureRandom();
        byte message[] = new byte[256];

        for (int i = 0; i < 1000_000; i ++) {
            random.nextBytes(message);

            // make a random keypair
            Keypair keypair = Keypair.ellipticCurvePair();
            // sign
            Signature signature = keypair.sign(message);
            // verify signature
            // verify key
            Assertions.assertTrue(signature.recover(message).compareTo(keypair.getPublicKey()) == 0, "could not recover public key from signature");
        }
    }
}
