package org.wolkenproject;

import org.wolkenproject.core.Context;
import org.wolkenproject.core.transactions.Transaction;

public class PendingTransaction implements Comparable<PendingTransaction> {
    private final Transaction   transaction;
    private final long          when;

    public PendingTransaction(Transaction transaction, long when) {
        this.transaction = transaction;
        this.when = when;
    }

    public boolean isInvalid() {
        return !transaction.shallowVerify();
    }

    public boolean shouldDelete() {
        return System.currentTimeMillis() - when > Context.getInstance().getNetworkParameters().getMaxTransactionRejectionTime();
    }

    @Override
    public int compareTo(PendingTransaction o) {
        boolean isInvalid = isInvalid();
        boolean oIsInvalid = o.isInvalid();

        if (isInvalid && oIsInvalid) {
            return when < o.when ? -1 : 1;
        }

        if (isInvalid) {
            return 1;
        }

        if (oIsInvalid) {
            return -1;
        }

        return -1;
    }

    public Long calculateSize() {
        return transaction.calculateSize();
    }

    public byte[] getHash() {
        return transaction.getHash();
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
