package org.wolkenproject.crypto;

import org.wolkenproject.serialization.SerializableI;

public abstract class Signature extends SerializableI {
    public abstract boolean checkSignature(byte originalMessage[]);
}
