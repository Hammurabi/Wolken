package org.wolkenproject.core.events;

import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;
import org.wolkenproject.utils.Utils;

public class WithdrawFundsEvent extends Event {
    private byte address[];
    private long amount;

    @Override
    public void apply() {
        Context.getInstance().getDatabase().updateAccount(address,
                Context.getInstance().getDatabase().getAccount(address).withdraw(amount));
    }

    @Override
    public void undo() {
        Context.getInstance().getDatabase().updateAccount(address,
                Context.getInstance().getDatabase().getAccount(address).deposit(amount));
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Withdraw".getBytes(), address, Utils.concatenate(amount));
    }
}
