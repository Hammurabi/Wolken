package org.wolkenproject.wallet;

import org.wolkenproject.crypto.CryptoUtil;
import org.wolkenproject.crypto.Key;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.ec.ECKeypair;
import org.wolkenproject.crypto.ec.ECPrivateKey;
import org.wolkenproject.utils.FileService;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Tuple;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.SecureRandom;

public class BasicWallet {
    private Keypair     keypair;
    private FileService fileService;

    public BasicWallet(FileService service) {
    }

    private void setPublicKey(Key key) {
    }

    private void setPrivateKey(Key key) {
    }

    public Key getPrivateKey() {
        return getPrivateKey(null);
    }

    public Key getPrivateKey(byte password[]) {
        return ;
    }

    public static BasicWallet generateWallet(FileService service) {
        if (!service.exists()) {
            byte random[] = new SecureRandom().generateSeed(1024);

            byte privateKey[] = HashUtil.sha256d(random);
            Key publicKey = ECKeypair.publicKeyFromPrivate(new BigInteger(1, privateKey));

            Tuple<byte[], byte[]> eiv   = CryptoUtil.aesEncrypt();
            byte encodedPrivate[]       = null;

            return new BasicWallet(publicKey, encodedPrivate);
        }

        return null;
    }
}
