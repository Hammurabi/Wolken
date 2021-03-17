package org.wolkenproject.wallet;

import org.wolkenproject.crypto.AESResult;
import org.wolkenproject.crypto.CryptoUtil;
import org.wolkenproject.crypto.Key;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.ec.ECKeypair;
import org.wolkenproject.crypto.ec.ECPrivateKey;
import org.wolkenproject.crypto.ec.ECPublicKey;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.FileService;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.VarInt;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

    public BasicWallet(FileService newFile) throws IOException, WolkenException {
        InputStream stream = fileService.openFileInputStream();
        int version = VarInt.readCompactUInt32(false, stream);
        byte pub[]  = new byte[65];
        int read = stream.read(pub);
        if (read != pub.length) {
            throw new WolkenException("could not read entire public key.");
        }

        publicKey = new ECPublicKey(pub);
        stream.close();
    }

    public Key getPrivateKey() {
        return getPrivateKey(null);
    }

    public Key getPrivateKey(char password[]) {
        try {
            InputStream stream = fileService.openFileInputStream();
            int version = VarInt.readCompactUInt32(false, stream);
            byte pub[]  = new byte[65];
            int read = stream.read(pub);
            if (read != pub.length) {
                throw new WolkenException("could not read entire public key.");
            }
            byte salt[] = new byte[8];
            read = stream.read(salt);
            byte iv[] = new byte[16];
            read = stream.read(iv);
            byte enc[] = new byte[48];
            read = stream.read(enc);

            byte key[] = CryptoUtil.aesDecrypt(enc, CryptoUtil.generateSecretForAES(password, salt), iv);
            stream.close();

            return new ECPrivateKey(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

            // write a version number
            VarInt.writeCompactUInt32(1, false, stream);
            // does not need to be encrypted (65 bytes)
            stream.write(publicKey.getEncoded());
            // does not need to be secret (8 bytes)
            stream.write(salt);
            // does not need to be secret (16 bytes)
            stream.write(result.getIv());
            // must be encrypted (48 bytes)
            stream.write(result.getEncryptionResult());

            return new BasicWallet(publicKey, service);
        }

        return null;
    }
}
