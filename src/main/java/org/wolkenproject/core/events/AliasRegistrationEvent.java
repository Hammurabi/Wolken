package org.wolkenproject.core.events;

import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;

public class AliasRegistrationEvent extends Event {
    private byte    address[];
    private long    alias;

    public AliasRegistrationEvent(byte[] address, long alias) {
        super();
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().registerAlias(address, alias);
    }

    @Override
    public void undo() {
    }

    @Override
    public byte[] getEventBytes() {
        return new byte[0];
    }
}
