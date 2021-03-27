package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class Asset extends SerializableI {
    // 20 byte unique identifier (hash160 of constructor contract/transaction).
    private final byte      assetIdentifier[];
    // 256bit unsigned integer representing the total supply of this asset.
    private final BigInteger totalSupply;

    public Asset(byte uuid[], BigInteger totalSupply) {
        this.assetIdentifier    = uuid;
        this.totalSupply        = totalSupply;
    }

    public byte[] getAssetIdentifier() {
        return assetIdentifier;
    }

    public BigInteger getTotalSupply() {
        return totalSupply;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        stream.write(assetIdentifier);
        VarInt.writeCompactUint256(totalSupply, false, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        checkFullyRead(stream.read(assetIdentifier), Address.RawLength);
        VarInt.readCompactUint256(false, stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Asset(new byte[Address.RawLength], BigInteger.ONE);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Asset.class);
    }
}
