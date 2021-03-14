package org.wolkenproject.crypto.ec;

import org.wolkenproject.core.Context;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
}