package org.wolkenproject.core.events;

import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;

public class AliasedAccountBalanceUpdateEvent extends Event {
    private long alias;
    private long value;

    public AliasedAccountBalanceUpdateEvent(long alias, long value) {
        super();
        this.alias = alias;
        this.value = value;
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().updateAccount(
                Context.getInstance().getDatabase().getAccountHolder(alias),
                Context.getInstance().getDatabase().getAccount(alias).updateBalance(value));
    }

    @Override
    public void undo() {
        Context.getInstance().getDatabase().updateAccount(
                Context.getInstance().getDatabase().getAccountHolder(alias),
                Context.getInstance().getDatabase().getAccount(alias).updateBalance(-value));
    }

    @Override
    public byte[] getEventBytes() {
        return new byte[0];
    }
}
