package org.wolkenproject.core;

import java.util.Collection;
import java.util.Set;

public class PrunedBlock {
    private final BlockHeader           blockHeader;
    private final Collection<byte[]>    transactions;
    private final Collection<byte[]>    events;

    public PrunedBlock(BlockHeader blockHeader, Collection<byte[]> transactions, Collection<byte[]> events) {
        this.blockHeader = blockHeader;
        this.transactions = transactions;
        this.events = events;
    }

    public Collection<byte[]> getTransactions() {
        return transactions;
    }

    public Collection<byte[]> getEvents() {
        return events;
    }

    public boolean containsTransaction(byte transactionId[]) {
        return transactions.contains(transactionId);
    }

    public boolean containsEvent(byte eventId[]) {
        return events.contains(eventId);
    }
}
