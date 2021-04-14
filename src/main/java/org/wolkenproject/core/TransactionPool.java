package org.wolkenproject.core;

import org.wolkenproject.PendingTransaction;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.utils.ByteArray;
import org.wolkenproject.utils.HashQueue;
import org.wolkenproject.utils.LinkedHashQueue;
import org.wolkenproject.utils.VoidCallable;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionPool {
    private HashQueue<PendingTransaction>       pendingTransactions;
    private HashQueue<RejectedTransaction>      rejectedTransactions;
    private ReentrantLock                       mutex;
    private static final int                    MaximumTransactionQueueSize = 1_250_000_000;
    private static final int                    MaximumRejectionQueueSize   =   500_000_000;
    private final Emitter<PendingTransaction>   pendingTransactionEmitter;
    private final Emitter<RejectedTransaction>  rejectedTransactionEmitter;
    private final Emitter<PendingTransaction>   pendingTimedoutEmitter;
    private final Emitter<RejectedTransaction>  rejectedTimedoutEmitter;

    public TransactionPool() {
        pendingTransactions     = new LinkedHashQueue<>(PendingTransaction::calculateSize);
        rejectedTransactions    = new LinkedHashQueue<>(RejectedTransaction::calculateSize);
        mutex                   = new ReentrantLock();
        pendingTransactionEmitter = new Emitter<>();
        rejectedTransactionEmitter = new Emitter<>();
        pendingTimedoutEmitter = new Emitter<>();
        rejectedTimedoutEmitter = new Emitter<>();
    }

    public boolean contains(byte[] txid) {
        mutex.lock();
        try {
            return pendingTransactions.containsKey(ByteArray.wrap(txid)) || rejectedTransactions.containsKey(ByteArray.wrap(txid));
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

    public PendingTransaction getTransaction(byte[] txid) {
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
                PendingTransaction transaction = pendingTransactions.poll();
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
            addInternally(new PendingTransaction(transaction, System.currentTimeMillis()));
        } finally {
            mutex.unlock();
        }
    }

    protected void addInternally(PendingTransaction transaction) {
        pendingTransactions.add(transaction, transaction.getHash());
        pendingTransactionEmitter.call(transaction);
        while (pendingTransactions.byteCount() > MaximumTransactionQueueSize) {
            // pop the transaction from the back of the queue.
            PendingTransaction pendingTransaction = pendingTransactions.pop();
            pendingTimedoutEmitter.call(pendingTransaction);
            rejectInternally(pendingTransaction);
        }
    }

    private void rejectInternally(PendingTransaction pendingTransaction) {
        RejectedTransaction rejectedTransaction = new RejectedTransaction(pendingTransaction, System.currentTimeMillis());
        rejectedTransactions.add(rejectedTransaction, pendingTransaction.getHash());
        rejectedTransactionEmitter.call(rejectedTransaction);
        while (rejectedTransactions.byteCount() > MaximumTransactionQueueSize) {
            // pop the transaction from the back of the queue.
            RejectedTransaction rejected = rejectedTransactions.poll();
            rejectedTimedoutEmitter.call(rejected);
        }
    }

    public void add(Set<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            add(transaction);
        }
    }

    public PendingTransaction poll() {
        mutex.lock();
        try {
            return pollInternally();
        } finally {
            mutex.unlock();
        }
    }

    private PendingTransaction pollInternally() {
        if (rejectedTransactions.size() > 0) {
            // poll a transaction, this will return the earliest (most likely valid) transaction.
            RejectedTransaction transaction = rejectedTransactions.peek();
            if (!transaction.isInvalid()) {
                rejectedTransactions.poll();
            } else {
                return transaction.getTransaction();
            }
        }


    }

    public void fillBlock(Block block) {
        mutex.lock();
        try {
            while (block.calculateSize() < Context.getInstance().getNetworkParameters().getMaxBlockSize() && pendingTransactions.hasElements()) {
                PendingTransaction transaction = pendingTransactions.poll();
                if (transaction.shallowVerify()) {
                    block.addTransaction(transaction);

                    if (block.calculateSize() > Context.getInstance().getNetworkParameters().getMaxBlockSize()) {
                        block.removeLastTransaction();
                        addInternally(transaction);
                        break;
                    }
                } else {
                    rejectedTransactions.add(new RejectedTransaction(transaction, System.currentTimeMillis()), transaction.getHash());
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
                PendingTransaction transaction = pendingTransactions.poll();
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

    public void registerPendingTransactionListener(VoidCallable<PendingTransaction> listener) {
        this.pendingTransactionEmitter.add(listener);
    }

    public void registerRejectedTransactionListener(VoidCallable<RejectedTransaction> listener) {
        this.rejectedTransactionEmitter.add(listener);
    }

    public void registerPendingTransactionTimeoutListener(VoidCallable<PendingTransaction> listener) {
        this.pendingTimedoutEmitter.add(listener);
    }

    public void registerRejectedTransactionTimeoutListener(VoidCallable<RejectedTransaction> listener) {
        this.rejectedTimedoutEmitter.add(listener);
    }
}