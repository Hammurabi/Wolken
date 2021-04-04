package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class BlockMetadata extends SerializableI {
    private int height;
    private int transactionCount;
    private int eventCount;
    private long totalValue;
    private long fees;
    private BigInteger chainWork;

    public BlockMetadata() {
        this(0, 0, 0, 0, 0, BigInteger.ZERO);
    }

    public BlockMetadata(int height, int transactionCount, int eventCount, long totalValue, long fees, BigInteger chainWork) {
        this.height = height;
        this.transactionCount = transactionCount;
        this.eventCount = eventCount;
        this.totalValue = totalValue;
        this.fees = fees;
        this.chainWork = chainWork;
    }

    public int getHeight() {
        return height;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public int getEventCount() {
        return eventCount;
    }

    public long getTotalValue() {
        return totalValue;
    }

    public long getFees() {
        return fees;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return new BlockMetadata();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(BlockMetadata.class);
    }
}
