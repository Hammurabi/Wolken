package org.wolkenproject.crypto;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

public abstract class Signature extends SerializableI {
    public abstract boolean checkSignature(byte[] originalMessage, Key publicKey);
    public Key recover(byte originalMessage[]) throws WolkenException {
        return null;
    }
}
