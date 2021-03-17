package org.wolkenproject.wallet;

import org.wolkenproject.crypto.AESResult;
import org.wolkenproject.crypto.CryptoUtil;
import org.wolkenproject.crypto.Key;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.ec.ECKeypair;
import org.wolkenproject.crypto.ec.ECPrivateKey;
import org.wolkenproject.utils.FileService;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Tuple;
import org.wolkenproject.utils.VarInt;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

public class BasicWallet {
    private Key         publicKey;
    private FileService fileService;

    public BasicWallet(Key publicKey, FileService service) {
        this.publicKey  = publicKey;
        this.fileService= service;
    }

    public Key getPrivateKey() {
        return getPrivateKey(null);
    }

    public Key getPrivateKey(byte password[]) {
        return null;
    }

    public static BasicWallet generateWallet(FileService service) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidKeySpecException, IOException {
        return generateWallet(service, new char[] {0, 0, 0, 0});
    }

    public static BasicWallet generateWallet(FileService service, char password[]) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidKeySpecException, IOException {
        if (!service.exists()) {
            byte random[] = new SecureRandom().generateSeed(1024);

            byte privateKey[] = HashUtil.sha256d(random);
            Key publicKey = ECKeypair.publicKeyFromPrivate(new BigInteger(1, privateKey));

            byte salt[]                 = new byte[8];
            new SecureRandom().nextBytes(salt);
            SecretKey encryptionKey     = CryptoUtil.generateSecretForAES(password, salt);
            AESResult result            = CryptoUtil.aesEncrypt(privateKey, encryptionKey);

            OutputStream stream         = service.openFileOutputStream();
            VarInt.writeCompactUInt32(1, false, stream);
            stream.write(publicKey.getEncoded());
            stream.write(result.getIv());
            stream.write(result.getEncryptionResult());

            return new BasicWallet(publicKey, service);
        }

        return null;
    }
}
