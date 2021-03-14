package org.wolkenproject.core;

import org.wolkenproject.serialization.SerializableI;

public class Account extends SerializableI {
    private final long nonce;
    private final long balance;

    public Account(long nonce, long balance) {
        this.nonce      = nonce;
        this.balance    = balance;
    }
}
