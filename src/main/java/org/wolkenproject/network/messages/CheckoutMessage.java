package org.wolkenproject.network.messages;

import org.wolkenproject.core.Context;
import org.wolkenproject.network.Message;

public class CheckoutMessage extends Message {
    public static final class Reason {
        public static final int
            None = 0,
            SelfConnet = 1;
    }

    private int reason;

    public CheckoutMessage(int reason) {
        super(Flags.Notify, Context.getInstance().getNetworkParameters().getVersion());
    }
}
