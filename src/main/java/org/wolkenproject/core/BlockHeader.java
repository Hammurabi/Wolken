package org.wolkenproject.core;

import org.json.JSONObject;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;

import static org.wolkenproject.utils.HashUtil.sha256d;

public class BlockHeader extends SerializableI {
    public static int Size = 80;
    private int version;
    private byte previousHash[];
    private byte merkleRoot[];
    private int timestamp;
    private int bits;
    private int nonce;

    public BlockHeader() {
        this(0, 0, new byte[32], new byte[32], 0, 0);
    }

    public BlockHeader(int version, int timestamp, byte[] previousHash, byte[] merkleRoot, int bits, int nonce) {
        this.version = version;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.merkleRoot = merkleRoot;
        this.bits = bits;
        this.nonce = nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    protected void setMerkleRoot(byte[] merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public void setParent(byte[] hash) {
        this.previousHash = hash;
    }

    public void setBits(int bits) {
        this.bits = bits;
    }

    public int getVersion() {
        return version;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public byte[] getParentHash() {
        return previousHash;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public int getNonce() {
        return nonce;
    }

    public int getBits() {
        return bits;
    }

    public byte[] getHeaderBytes() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            writeBlockHeader(outputStream);
            outputStream.flush();
            outputStream.close();

            return outputStream.toByteArray();
        } catch (IOException | WolkenException e) {
            return null;
        }
    }

    public byte[] getHashCode() {
        return sha256d(getHeaderBytes());
    }

    public BlockHeader clone() {
        return new BlockHeader(version, timestamp, Arrays.copyOf(previousHash, 32), Arrays.copyOf(merkleRoot, 32), bits, nonce);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        writeBlockHeader(stream);
    }

    public void writeBlockHeader(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(version, stream);
        Utils.writeInt(timestamp, stream);
        stream.write(previousHash);
        stream.write(merkleRoot);
        Utils.writeInt(bits, stream);
        Utils.writeInt(nonce, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        version     = Utils.readInt(stream);
        timestamp   = Utils.readInt(stream);
        checkFullyRead(stream.read(previousHash), Block.UniqueIdentifierLength);
        checkFullyRead(stream.read(merkleRoot), Block.UniqueIdentifierLength);
        bits        = Utils.readInt(stream);
        nonce       = Utils.readInt(stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new BlockHeader();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(BlockHeader.class);
    }

    public boolean verifyProofOfWork() {
        byte hash[] = getHashCode();
        byte bits[] = targetFromBits(Utils.takeApart(this.bits));
        BigInteger result = new BigInteger(1, hash);
        BigInteger target = new BigInteger(1, bits);
        return result.compareTo(target) < 0;
    }

    public static byte[] targetFromBits(byte[] bits) {
        byte target[]   = new byte[32];
        int offset      = 32 - Byte.toUnsignedInt(bits[0]);
        target[offset + 0]  = bits[1];
        target[offset + 1]  = bits[2];
        target[offset + 2]  = bits[3];

        return target;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("version", version)
                .put("parent", Base16.encode(previousHash))
                .put("merkleRoot", Base16.encode(merkleRoot))
                .put("timestamp", timestamp)
                .put("bits", bits)
                .put("nonce", nonce);
    }
}
