package org.wolkenproject.core;

import org.wolkenproject.PendingTransaction;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.utils.Tuple;
import org.wolkenproject.utils.VoidCallable;

public class ListenerService {
    private final Context context;

    public ListenerService(Context context) {
        this.context = context;
    }

    public static void register(int verbosity) {
        if (verbosity > 0) {
            listenForImportantMessages();
        }

        if (verbosity > 1) {
            listenForAlertMessages();
        }

        if (verbosity > 2) {
            listenForNotificationMessages();
        }

        if (verbosity > 3) {
            journal();
        }

        if (verbosity > 4) {
            debug();
        }
    }

    private static void listenForImportantMessages() {
    }

    private static void listenForAlertMessages() {
    }

    private static void listenForNotificationMessages() {
    }

    private static void journal() {
    }

    private static void debug() {
    }

    public void registerNodeDisconnectionListener(VoidCallable<Node> listener) {
        context.getServer().registerDisconnectListener(listener);
    }

    public void registerMessageSendListener(VoidCallable<Tuple<Message, Node>> listener) {
        Node.registerMessageSendListener(listener);
    }

    public void registerInboundConnectionListener(VoidCallable<Node> listener) {
        context.getServer().registerInboundListener(listener);
    }

    public void registerOutboundConnectionListener(VoidCallable<Node> listener) {
        context.getServer().registerOutboundListener(listener);
    }

    public void registerPendingTransactionListener(VoidCallable<PendingTransaction> listener) {
        context.getTransactionPool().registerPendingTransactionListener(listener);
    }

    public void registerPendingTransactionTimeoutListener(VoidCallable<PendingTransaction> listener) {
        context.getTransactionPool().registerPendingTransactionTimeoutListener(listener);
    }

    public void registerRejectedTransactionListener(VoidCallable<UnconfirmedTransaction> listener) {
        context.getTransactionPool().registerRejectedTransactionListener(listener);
    }

    public void registerRejectedTransactionTimeoutListener(VoidCallable<UnconfirmedTransaction> listener) {
        context.getTransactionPool().registerRejectedTransactionTimeoutListener(listener);
    }
}
