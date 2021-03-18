package org.wolkenproject.core.events;

import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;

public class AliasedAccountBalanceUpdateEvent extends Event {
    public AliasedAccountBalanceUpdateEvent(long alias, long value) {
        super();
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().updateAccount(alias, Context.getInstance().getDatabase().getAccount(alias).updateBalance(value));
    }

    @Override
    public void undo() {
        Context.getInstance().getDatabase().updateAccount(alias, Context.getInstance().getDatabase().getAccount(alias).updateBalance(-value));
    }

    @Override
    public byte[] getEventBytes() {
        return new byte[0];
    }
}
