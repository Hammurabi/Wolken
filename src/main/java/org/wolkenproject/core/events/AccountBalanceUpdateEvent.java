package org.wolkenproject.core.events;

import org.wolkenproject.core.Event;
import org.wolkenproject.utils.Utils;

public class AccountBalanceUpdateEvent extends Event {
    private byte[]  recipient;
    private long    value;

    public AccountBalanceUpdateEvent(byte[] recipient, long value) {
        super();
    }

    @Override
    public void apply() {
    }

    @Override
    public void undo() {
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate(recipient, Utils.takeApartLong(value));
    }
}
