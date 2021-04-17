package org.wolkenproject.core;

import org.wolkenproject.PendingTransaction;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.utils.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.wolkenproject.utils.Logger.Levels.Journaling;

public class TransactionPool {
    private HashQueue<PendingTransaction>       pendingTransactions;
    private HashQueue<RejectedTransaction>      rejectedTransactions;
    private ReentrantLock                       mutex;
    private static final int                    MaximumTransactionQueueSize = 1_250_000_000;
    private static final int                    MaximumRejectionQueueSize   =   500_000_000;

    public TransactionPool() {
        pendingTransactions     = new PriorityHashQueue<>(PendingTransaction::calculateSize);
        rejectedTransactions    = new PriorityHashQueue<>(RejectedTransaction::calculateSize);
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

    public Set<byte[]> getHeadOfQueue() {
        mutex.lock();
        try {
            Set<byte[]> txids = new LinkedHashSet<>();
            int count = Math.min(1024, pendingTransactions.size());
            List<PendingTransaction> txs = new ArrayList<>();

            for (int i = 0; i < count; i ++) {
                PendingTransaction transaction = pendingTransactions.poll();
                txs.add(transaction);
                txids.add(transaction.getHash());
            }

            for (PendingTransaction transaction : txs) {
                pendingTransactions.add(transaction, transaction.getHash());
            }

            return txids;
        } finally {
            mutex.unlock();
        }
    }

    public void add(Transaction transaction) {
        add(new PendingTransaction(transaction, System.currentTimeMillis()));
    }

    public void add(PendingTransaction transaction) {
        mutex.lock();
        try {
            addInternally(transaction);
        } finally {
            mutex.unlock();
        }
    }

    protected void addInternally(PendingTransaction transaction) {
        pendingTransactions.add(transaction, transaction.getHash());
        Logger.notify("added transaction '${transaction}'", Journaling, transaction);
        while (pendingTransactions.byteCount() > MaximumTransactionQueueSize) {
            // pop the transaction from the back of the queue.
            PendingTransaction pendingTransaction = pendingTransactions.pop();
            rejectInternally(pendingTransaction);
        }
    }

    private void rejectInternally(PendingTransaction pendingTransaction) {
        RejectedTransaction rejectedTransaction = new RejectedTransaction(pendingTransaction, System.currentTimeMillis());
        rejectedTransactions.add(rejectedTransaction, pendingTransaction.getHash());
        Logger.notify("added transaction '${transaction}'", Journaling, rejectedTransaction);
        while (rejectedTransactions.byteCount() > MaximumTransactionQueueSize) {
            // pop the earliest transaction from the top of the queue.
            RejectedTransaction rejected = rejectedTransactions.poll();
            // if the transaction is valid again then put it back into the queue.
            if (!rejected.isInvalid()) {
                addInternally(rejectedTransaction.getTransaction());
            }
            
            Logger.notify("timed out transaction '${transaction}'", Journaling, rejected);
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
            if (transaction.isInvalid() && transaction.shouldDelete()) {
                // permanently remove transaction.
                rejectedTransactions.poll();
            }

            if (!transaction.isInvalid()) {
                return rejectedTransactions.poll().getTransaction();
            }
        }

        while (pendingTransactions.size() > 0) {
            // poll a transaction from the top.
            PendingTransaction pendingTransaction = pendingTransactions.poll();

            // if the transaction is now invalid due to it being included in a block, then reject it.
            if (pendingTransaction.isInvalid()) {
                rejectInternally(pendingTransaction);
                continue;
            }

            return pendingTransaction;
        }

        return null;
    }

    public void fillBlock(Block block) {
        while (block.calculateSize() < Context.getInstance().getContextParams().getMaxBlockSize() && hasPending()) {
            PendingTransaction transaction = pollTransaction();
            block.addTransaction(transaction.getTransaction());

            if (block.calculateSize() > Context.getInstance().getContextParams().getMaxBlockSize()) {
                block.removeLastTransaction();
                add(transaction);
                break;
            }
        }
    }

    private boolean hasPending() {
        mutex.lock();
        try {
            return pendingTransactions.hasElements() || rejectedTransactions.hasElements();
        } finally {
            mutex.unlock();
        }
    }

    public PendingTransaction pollTransaction() {
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
}