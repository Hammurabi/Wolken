package org.wolkenproject.core.events;

import org.wolkenproject.core.Event;

public class AliasedAccountBalanceUpdateEvent extends Event {
    public AliasedAccountBalanceUpdateEvent(long alias, long value) {
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
        return new byte[0];
    }
}
