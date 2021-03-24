package org.wolkenproject.wallet;

import org.wolkenproject.core.Address;
import org.wolkenproject.crypto.Key;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.ec.ECKeypair;
import org.wolkenproject.crypto.ec.ECPrivateKey;

public class Wallet {
    private final byte    privateKey[];
    private final Key     publicKey;
    private final Address address;
    private long          nonce;

    public Wallet(byte[] privateKey, Key publicKey, Address address, long nonce) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.address = address;
        this.nonce = nonce;
    }

    public Keypair getKeypairForSigning(char password[]) {
        byte privateBytes[] = privateKey;
        if (password != null) {
        }

        return new ECKeypair(new ECPrivateKey(privateBytes), publicKey);
    }
}
