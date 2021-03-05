package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static org.wolkenproject.utils.HashUtil.sha256d;
import static org.wolkenproject.utils.Utils.concatenate;

public class BlockHeader extends SerializableI {
    private int         version;
    private long        timestamp;
    private byte        previousHash[];
    private byte        merkleRoot[];
    private int         bits;
    private int         nonce;

    public BlockHeader() {
        this(0, 0, new byte[32], new byte[32], 0, 0);
    }

    public BlockHeader(int version, long timestamp, byte[] previousHash, byte[] merkleRoot, int bits, int nonce) {
        this.version = version;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.merkleRoot = merkleRoot;
        this.bits = bits;
        this.nonce = nonce;
    }

    public void setNonce(int nonce)
    {
        this.nonce = nonce;
    }

    public int getVersion()
    {
        return version;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public byte[] getParentHash()
    {
        return previousHash;
    }

    public byte[] getMerkleRoot()
    {
        return merkleRoot;
    }

    public int getNonce() {
        return nonce;
    }

    public int getBits() {
        return bits;
    }

    public byte[] getHeaderBytes() {
        return concatenate(getBytesWithoutNonce(), Utils.takeApart(nonce));
    }

    public byte[] getBytesWithoutNonce() {
        return concatenate(
                Utils.takeApart(version),
                Utils.takeApartLong(timestamp),
                previousHash,
                merkleRoot,
                Utils.takeApart(bits)
        );
    }

    public byte[] getHashCode() {
        return sha256d(getHeaderBytes());
    }

    public BlockHeader clone() {
        return new BlockHeader(version, timestamp, Arrays.copyOf(previousHash, 32), Arrays.copyOf(merkleRoot, 32), bits, nonce);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(version, stream);
        Utils.writeLong(timestamp, stream);
        stream.write(previousHash);
        stream.write(merkleRoot);
        stream.write(bits);
        Utils.writeInt(nonce, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        byte buffer[] = new byte[8];
        stream.read(buffer, 0, 4);
        version = Utils.makeInt(buffer);
        stream.read(buffer, 0, 8);
        timestamp = Utils.makeLong(buffer);

        stream.read(previousHash);
        stream.read(merkleRoot);

        stream.read(buffer, 0, 4);
        nonce = Utils.makeInt(buffer);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new BlockHeader();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(BlockHeader.class);
    }
}
