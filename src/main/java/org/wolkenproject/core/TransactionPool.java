package org.wolkenproject.core;

import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.utils.PriorityHashQueue;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionPool {
    private PriorityHashQueue<Transaction> transactions;
    private ReentrantLock                   mutex;
    private static final int                MaximumTransactionQueueSize = 1_250_000_000;

    public TransactionPool() {
        transactions    = new PriorityHashQueue<>(Transaction.class);
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

    public Transaction getTransaction(byte[] txid) {
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

    public void add(Set<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            this.transactions.add(transaction);
        }
    }

    public void queueBlock(Block block) {
        for (Transaction transaction : block) {
            byte txid[] = transaction.getTransactionID();
            if (transactions.containsKey(txid)) {
                continue;
            }

            if (Context.getInstance().getDatabase().checkTransactionExists(txid)) {
                continue;
            }

            transactions.add(transaction);
        }
    }

    public Transaction pollTransaction() {
        mutex.lock();
        try {
            // infinitely loop
            while (!transactions.isEmpty()) {
                // poll a transaction
                Transaction transaction = transactions.poll();
                byte txid[] = transaction.getTransactionID();

                // if the transaction has already been added to a block then continue
                if (Context.getInstance().getDatabase().checkTransactionExists(txid)) {
                    continue;
                }

                // otherwise return the transaction
                return transaction;
            }

            return null;
        } finally {
            mutex.unlock();
        }
    }
}