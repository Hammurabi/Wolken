package org.wokenproject.core;

import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class Block extends SerializableI {
    public static int UniqueIdentifierLength = 32;
    private BlockHeader         header;
    private Set<TransactionI>   transactions;

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

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        header.write(stream);
        Utils.writeInt(transactions.size(), stream);
        for (TransactionI transaction : transactions)
        {
            transaction.write(stream);
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return null;
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Block.class);
    }
}
