package org.wolkenproject.crypto;

import org.wolkenproject.serialization.SerializableI;

import java.math.BigInteger;

public abstract class Signature extends SerializableI {
    public abstract boolean checkSignature(byte originalMessage[]);
    public BigInteger recover() {
        return null;
    }
}
