package org.wolkenproject.core;

import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.utils.ByteArray;
import org.wolkenproject.utils.HashQueue;
import org.wolkenproject.utils.PriorityHashQueue;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionPool {
    private HashQueue<Transaction>          pendingTransactions;
    private HashQueue<RejectedTransaction>  rejectedTransactions;
    private ReentrantLock                   mutex;
    private static final int                MaximumTransactionQueueSize = 1_250_000_000;

    public TransactionPool() {
        pendingTransactions     = new PriorityHashQueue<>(Transaction::calculateSize);
        rejectedTransactions    = new PriorityHashQueue<>(RejectedTransaction::calculateSize);
        mutex           = new ReentrantLock();
    }

    public boolean contains(byte[] txid) {
        mutex.lock();
        try {
            return pendingTransactions.containsKey(ByteArray.wrap(txid));
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
            return pendingTransactions.getByHash(ByteArray.wrap(txid));
        } finally {
            mutex.unlock();
        }
    }

    public Set<byte[]> getInv() {
        mutex.lock();
        try {
            Set<byte[]> txids = new LinkedHashSet<>();
            int count = Math.min(1024, pendingTransactions.size());
            List<Transaction> txs = new ArrayList<>();

            for (int i = 0; i < count; i ++) {
                Transaction transaction = pendingTransactions.poll();
                txs.add(transaction);

                txids.add(transaction.getHash());
            }

            for (Transaction transaction : txs) {
                pendingTransactions.add(transaction, transaction.getHash());
            }

            return txids;
        } finally {
            mutex.unlock();
        }
    }

    public void add(Transaction transaction) {
        mutex.lock();
        try {
            addInternally(transaction);
        } finally {
            mutex.unlock();
        }
    }

    protected void addInternally(Transaction transaction) {
        pendingTransactions.add(transaction, transaction.getHash());
        if (pendingTransactions.byteCount() > MaximumTransactionQueueSize) {
            pendingTransactions.removeTails(pendingTransactions.size() - 1);
        }
    }

    public void add(Set<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            add(transaction);
        }
    }

    public void fillBlock(Block block) {
        mutex.lock();
        try {
            while (block.calculateSize() < Context.getInstance().getNetworkParameters().getMaxBlockSize() && pendingTransactions.hasElements()) {
                Transaction transaction = pendingTransactions.poll();
                block.addTransaction(transaction);

                if (block.calculateSize() > Context.getInstance().getNetworkParameters().getMaxBlockSize()) {
                    block.removeLastTransaction();
                    addInternally(transaction);
                    break;
                }
            }
        } finally {
            mutex.unlock();
        }
    }

    public Transaction pollTransaction() {
        mutex.lock();
        try {
            // infinitely loop
            while (!pendingTransactions.isEmpty()) {
                // poll a transaction
                Transaction transaction = pendingTransactions.poll();
                byte txid[] = transaction.getHash();

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