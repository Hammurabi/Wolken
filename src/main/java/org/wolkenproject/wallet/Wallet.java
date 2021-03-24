package org.wolkenproject.wallet;

import org.json.JSONObject;
import org.wolkenproject.core.Address;
import org.wolkenproject.crypto.CryptoUtil;
import org.wolkenproject.crypto.Key;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.ec.ECKeypair;
import org.wolkenproject.crypto.ec.ECPrivateKey;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Wallet {
    private final String  name;
    private final byte    privateKey[];
    private final Key     publicKey;
    private final Address address;
    private long          nonce;

    public Wallet(String name, byte[] privateKey, Key publicKey, Address address, long nonce) {
        this.name = name;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.address = address;
        this.nonce = nonce;
    }

    public Wallet(String name, char[] pass) {
        
    }

    public Keypair getKeypairForSigning(char password[]) throws WolkenException {
        byte privateBytes[] = privateKey;
        if (password != null) {
            byte salt[] = Utils.trim(privateBytes, 0, 8);
            byte iv[]   = Utils.trim(privateBytes, 8, 16);
            byte enc[]  = Utils.trim(privateBytes, 24, 48);

            try {
                privateBytes = CryptoUtil.aesDecrypt(enc, CryptoUtil.generateSecretForAES(password, salt), iv);
            } catch (InvalidKeySpecException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new WolkenException(e);
            }
        }

        return new ECKeypair(new ECPrivateKey(privateBytes), publicKey);
    }

    public String getName() {
        return name;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public Address getAddress() {
        return address;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("privatekey", Base16.encode(privateKey));
        json.put("publickey", Base16.encode(publicKey.getEncoded()));
        json.put("address", Base58.encode(address.getFormatted()));
        json.put("nonce", nonce);

        return json;
    }
}
