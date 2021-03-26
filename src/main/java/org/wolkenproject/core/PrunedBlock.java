package org.wolkenproject.core;

import java.util.List;
import java.util.Set;

public class PrunedBlock {
    private final BlockHeader   blockHeader;
    private final Set<byte[]>   transactions;
    private final Set<byte[]>   events;

    public PrunedBlock(BlockHeader blockHeader, Set<byte[]> transactions, Set<byte[]> events) {
        this.blockHeader = blockHeader;
        this.transactions = transactions;
        this.events = events;
    }

    public Set<byte[]> getTransactions() {
        return transactions;
    }

    public Set<byte[]> getEvents() {
        return events;
    }

    public boolean containsTransaction(byte transactionId[]) {
        return transactions.contains(transactionId);
    }
}
