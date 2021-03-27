package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(transactions.size(), stream);
        Utils.writeInt(events.size(), stream);
        for (byte[] transaction : transactions) {
            stream.write(transaction);
        }
        for (byte[] event : events) {
            stream.write(event);
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
        return 0;
    }
}
