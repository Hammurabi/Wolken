package org.wolkenproject.core;

import org.wolkenproject.utils.PriorityHashQueue;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionPool {
    private PriorityHashQueue<TransactionI> transactions;
    private ReentrantLock                   mutex;
    private static final int                MaximumBlockQueueSize = 1_250_000_000;

    public TransactionPool() {
        transactions    = new PriorityHashQueue<>(TransactionI.class);
        mutex           = new ReentrantLock();
    }

    public boolean contains(byte[] txid) {
        mutex.lock();
        try {
            return transactions.containsKey(txid);
        } finally {
            mutex.unlock();
        }
    }

    public Set<byte[]> getNonDuplicateTransactions(Set<byte[]> list) {
        Set<byte[]> result = new HashSet<>();

        for (byte[] txid : list) {
            if (!contains(txid)) {
                result.add(txid);
            }
        }

        return result;
    }

    public TransactionI getTransaction(byte[] txid) {
        mutex.lock();
        try {
            return transactions.getByHash(txid);
        } finally {
            mutex.unlock();
        }
    }

    public Set<byte[]> getInv() {
        mutex.lock();
        try {
            Set<byte[]> txids = new LinkedHashSet<>();
            transactions.fillHashes(txids, 16_384);

            return txids;
        } finally {
            mutex.unlock();
        }
    }
}