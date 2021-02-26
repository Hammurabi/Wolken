package org.wokenproject.core;

public class Block {
    public static int UniqueIdentifierLength = 32;
    private BlockHeader header;

    public final int countLength() {
        return getBytes().length;
    }

    public final int getVersion() {
        return header.getVersion();
    }

    public final int getHeight() {
        return header.getHeight();
    }

    public final long getTimestamp() {
        return header.getTimeStamp();
    }

    public final byte[] getPreviousHash() {
        return header.getParentHash();
    }

    public final byte[] getMerkleRoot() {
        return header.getMerkleRoot();
    }

    public final byte[] getChainWork() {
        return header.getChainWork();
    }

    public final byte[] getBits() {
        return header.getBits();
    }

    public final int getNonce() {
        return header.getNonce();
    }

    public final byte[] getBytes() {
        return null;
    }

    public final byte[] getHashCode() {
        return getBlockHeader().getHashCode();
    }

    public final BlockHeader getBlockHeader() {
        return header;
    }
}
