package org.wolkenproject.core.events;

import org.wolkenproject.core.Event;
import org.wolkenproject.utils.Utils;

public class WithdrawFundsEvent extends Event {
    private byte address[];
    private long amount;
    
    @Override
    public void apply() {
    }

    @Override
    public void undo() {
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Withdraw".getBytes(), address, Utils.concatenate(amount));
    }
}
