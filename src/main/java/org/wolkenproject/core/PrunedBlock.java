package org.wolkenproject.core;

import org.wolkenproject.serialization.SerializableI;

import java.util.Collection;

public class PrunedBlock implements SerializableI {
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
