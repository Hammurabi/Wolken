package org.wolkenproject.core.events;

import org.wolkenproject.core.Event;

public class AccountBalanceUpdateEvent extends Event {
    public AccountBalanceUpdateEvent(byte[] recipient, long value) {
        super();
    }
}
