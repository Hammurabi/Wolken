package org.wokenproject.core;

import org.wokenproject.utils.Utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.wokenproject.utils.HashUtil.sha256d;
import static org.wokenproject.utils.Utils.concatenate;

public class BlockHeader {
    private final int   version;
    private final int   height;
    private final long  timestamp;
    private final byte  previousHash[];
    private final byte  merkleRoot[];
    private final byte  chainWorkHash[];
    private final byte  bits[];
    private int         nonce;

    public static final int SIZE = 4 + 4 + 8 + 32 + 32 + 32 + 4 + 4;
    public static final int SIZE_WITHOUT_NONCE = 4 + 8 + 8 + 32 + 32 + 32 + 4;

    public BlockHeader(byte bytes[]) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        this.version = buffer.getInt();
        this.height = buffer.getInt();
        this.timestamp = buffer.getLong();
        this.previousHash = new byte[32];
        this.merkleRoot = new byte[32];
        this.chainWorkHash = new byte[32];
        this.bits = new byte[4];

        buffer.get(previousHash);
        buffer.get(merkleRoot);
        buffer.get(chainWorkHash);
        this.nonce = buffer.getInt();
    }

    public BlockHeader(int version, int height, long timestamp, byte[] previousHash, byte[] merkleRoot, byte[] chainWorkHash, byte[] bits, int nonce) {
        this.version = version;
        this.height = height;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.merkleRoot = merkleRoot;
        this.chainWorkHash = chainWorkHash;
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

    public int getHeight()
    {
        return height;
    }

    public long getTimeStamp()
    {
        return timestamp;
    }

    public byte[] getParentHash()
    {
        return previousHash;
    }

    public byte[] getChainWork()
    {
        return chainWorkHash;
    }

    public byte[] getMerkleRoot()
    {
        return merkleRoot;
    }

    public int getNonce() {
        return nonce;
    }

    public byte[] getBits() {
        return bits;
    }

    public byte[] getBytes() {
        return concatenate(getBytesWithoutNonce(), Utils.takeApart(nonce));
    }

    public byte[] getBytesWithoutNonce() {
        return concatenate(new byte[] {
                        (byte) ((version >> 24) & 0xFF),
                        (byte) ((version >> 16) & 0xFF),
                        (byte) ((version >>  8) & 0xFF),
                        (byte) ((version) & 0xFF)
                }, new byte[] {
                        (byte) ((height >> 56) & 0xFF),
                        (byte) ((height >> 48) & 0xFF),
                        (byte) ((height >> 40) & 0xFF),
                        (byte) ((height >> 32) & 0xFF),
                        (byte) ((height >> 24) & 0xFF),
                        (byte) ((height >> 16) & 0xFF),
                        (byte) ((height >>  8) & 0xFF),
                        (byte) ((height) & 0xFF)
                }, new byte[] {
                        (byte) ((timestamp >> 56) & 0xFF),
                        (byte) ((timestamp >> 48) & 0xFF),
                        (byte) ((timestamp >> 40) & 0xFF),
                        (byte) ((timestamp >> 32) & 0xFF),
                        (byte) ((timestamp >> 24) & 0xFF),
                        (byte) ((timestamp >> 16) & 0xFF),
                        (byte) ((timestamp >>  8) & 0xFF),
                        (byte) ((timestamp) & 0xFF)
                },
                previousHash,
                merkleRoot,
                chainWorkHash,
                bits
        );
    }

    public byte[] getHashCode() {
        return sha256d(getBytes());
    }

    public BlockHeader clone() {
        return new BlockHeader(version, height, timestamp, Arrays.copyOf(previousHash, 32), Arrays.copyOf(merkleRoot, 32), Arrays.copyOf(chainWorkHash, 32), Arrays.copyOf(bits, 4), nonce);
    }
}
