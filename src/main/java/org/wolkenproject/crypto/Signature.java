package org.wolkenproject.crypto;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.math.BigInteger;

public abstract class Signature extends SerializableI {
    public abstract boolean checkSignature(byte[] originalMessage, BigInteger publicKey);
    public BigInteger recover(byte originalMessage[]) throws WolkenException {
        return null;
    }
}
