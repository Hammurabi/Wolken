package org.wolkenproject.core.events;

import org.wolkenproject.core.Event;

public class WithdrawFundsEvent extends Event {
    @Override
    public void apply() {
    }

    @Override
    public void undo() {
    }

    @Override
    public byte[] getEventBytes() {
        return new byte[0];
    }
}
