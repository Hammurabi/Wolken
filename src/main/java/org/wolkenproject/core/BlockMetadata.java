package org.wolkenproject.core;

import org.wolkenproject.serialization.SerializableI;

import java.math.BigInteger;

public class BlockMetadata extends SerializableI {
    private int height;
    private int transactionCount;
    private int eventCount;
    private int totalValue;
    private int fees;
    private BigInteger chainWork;

    public BlockMetadata(int height, int transactionCount, int eventCount, int totalValue, int fees, BigInteger chainWork) {
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

    public int getTotalValue() {
        return totalValue;
    }

    public int getFees() {
        return fees;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }
}
