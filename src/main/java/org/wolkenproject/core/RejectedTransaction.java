package org.wolkenproject.core;

import org.wolkenproject.PendingTransaction;

public class RejectedTransaction implements Comparable<RejectedTransaction> {
    private final PendingTransaction    transaction;
    private final long                  when;

    public RejectedTransaction(PendingTransaction transaction, long when) {
        this.transaction = transaction;
        this.when = when;
    }

    public boolean isInvalid() {
        return transaction.isInvalid();
    }

    public boolean shouldDelete() {
        return System.currentTimeMillis() - when > Context.getInstance().getContextParams().getMaxTransactionRejectionTime();
    }

    @Override
    public int compareTo(RejectedTransaction o) {
        boolean isInvalid = isInvalid();
        boolean oIsInvalid = o.isInvalid();

        if (isInvalid && oIsInvalid) {
            return transaction.compareTo(o.transaction);
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

    public PendingTransaction getTransaction() {
        return transaction;
    }
}
