package org.wolkenproject.core.events;

import org.wolkenproject.core.Account;
import org.wolkenproject.core.Event;

public class NewAccountEvent extends Event {
    private Account account;
    
    public NewAccountEvent(byte[] address, Account account) {
        super();
        this.account = account;
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
