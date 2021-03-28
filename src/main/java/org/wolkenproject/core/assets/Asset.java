package org.wolkenproject.core.assets;

import org.wolkenproject.core.Address;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public abstract class Asset extends SerializableI {
    // 20 byte unique identifier (hash160 of constructor contract/transaction).
    private final byte uuid[];

    // default constructor
    public Asset(byte uuid[]) {
        this.uuid = uuid;
    }

    public byte[] getUUID() {
        return uuid;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        stream.write(uuid);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        checkFullyRead(stream.read(uuid), Address.RawLength);
    }

    public abstract boolean isTransferable();
    public abstract boolean isFungible();
    public abstract BigInteger getTotalSupply();

    public abstract void writeContent(OutputStream stream) throws IOException, WolkenException;
    public abstract void readContent(InputStream stream) throws IOException, WolkenException;
}
