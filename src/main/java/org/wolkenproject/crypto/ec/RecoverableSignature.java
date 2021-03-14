package org.wolkenproject.crypto.ec;

import org.wolkenproject.core.Context;
import org.wolkenproject.crypto.CryptoLib;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Assertions;
import org.wolkenproject.utils.HashUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class RecoverableSignature extends Signature {
    private final byte v;
    private final byte r[];
    private final byte s[];

    public RecoverableSignature() {
        this((byte) 0, new byte[32], new byte[32]);
    }

    public RecoverableSignature(byte v, byte[] r, byte[] s) {
        this.v = v;
        this.r = r;
        this.s = s;
    }

    public byte getV() {
        return v;
    }

    public byte[] getR() {
        return r;
    }

    public byte[] getS() {
        return s;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RecoverableSignature();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(RecoverableSignature.class);
    }

    @Override
    public boolean checkSignature(byte[] originalMessage) {
        return false;
    }

    @Override
    public BigInteger recover(byte originalMessage[]) throws WolkenException {
        Assertions.assertTrue(r != null && r.length == 32, "r must be 32 bytes in length");
        Assertions.assertTrue(s != null && s.length == 32, "s must be 32 bytes in length");

        int header = v & 0xFF;

        if (header < 27 || header > 34) {
            throw new WolkenException("header byte out of range: " + header);
        }

        ECSig sig = new ECSig(new BigInteger(1, r), new BigInteger(1, s));
        BigInteger result = CryptoLib.recoverFromSignature(v - 27, sig, HashUtil.sha256d(originalMessage));

        if (result == null) {
            throw new WolkenException("could not recover public key.");
        }

        return result;
    }
}