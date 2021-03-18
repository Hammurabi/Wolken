package org.wolkenproject.core;

import org.wolkenproject.utils.Utils;

import java.util.List;
import java.util.Queue;

public class BlockStateChangeResult {
    private final Queue<byte[]> transactionIds;
    private final Queue<byte[]> transactionEventIds;
    private final List<Event>   transactionEvents;
    private final byte          merkleRoot[];
    private final byte          transactionMerkleRoot[];
    private final byte          transactionEventMerkleRoot[];

    public BlockStateChangeResult(Queue<byte[]> transactionIds, Queue<byte[]> transactionEventIds, List<Event> transactionEvents) {
        this.transactionIds             = transactionIds;
        this.transactionEventIds        = transactionEventIds;
        this.transactionEvents          = transactionEvents;
        this.transactionMerkleRoot      = Utils.calculateMerkleRoot(transactionIds);
        this.transactionEventMerkleRoot = Utils.calculateMerkleRoot(transactionEventIds);
        this.merkleRoot                 = Utils.calculateMerkleRoot(transactionMerkleRoot, transactionEventMerkleRoot);
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public byte[] getTransactionMerkleRoot() {
        return transactionMerkleRoot;
    }

    public byte[] getTransactionEventMerkleRoot() {
        return transactionEventMerkleRoot;
    }

    public List<byte[]> getTransactionIds() {
        return transactionIds;
    }

    public List<byte[]> getTransactionEventIds() {
        return transactionEventIds;
    }

    public List<Event> getTransactionEvents() {
        return transactionEvents;
    }
}
