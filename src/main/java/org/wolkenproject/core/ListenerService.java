package org.wolkenproject.core;

import org.wolkenproject.PendingTransaction;
import org.wolkenproject.utils.VoidCallable;

public class ListenerService {
    private final Context context;

    public ListenerService(Context context) {
        this.context = context;
    }

    public void registerPendingTransactionListener(VoidCallable<PendingTransaction> listener) {
        context.getTransactionPool().registerPendingTransactionListener(listener);
    }

    public void registerPendingTransactionTimeoutListener(VoidCallable<PendingTransaction> listener) {
        context.getTransactionPool().registerPendingTransactionTimeoutListener(listener);
    }

    public void registerRejectedTransactionListener(VoidCallable<RejectedTransaction> listener) {
        context.getTransactionPool().registerRejectedTransactionListener(listener);
    }

    public void registerRejectedTransactionTimeoutListener(VoidCallable<RejectedTransaction> listener) {
        context.getTransactionPool().registerRejectedTransactionTimeoutListener(listener);
    }
}
