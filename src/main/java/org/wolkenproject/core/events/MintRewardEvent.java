package org.wolkenproject.core.events;

import org.json.JSONObject;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.utils.Utils;

public class MintRewardEvent extends Event {
    private byte address[];
    private long amount;

    public MintRewardEvent(byte address[], long amount) {
        this.address    = address;
        this.amount     = amount;
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().updateAccount(address,
                Context.getInstance().getDatabase().findAccount(address).deposit(amount));
    }

    @Override
    public void undo() {
        Context.getInstance().getDatabase().updateAccount(address,
                Context.getInstance().getDatabase().findAccount(address).withdraw(amount));
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Mint".getBytes(), address, Utils.takeApartLong(amount));
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("event", this.getClass().getName()).put("address", Base58.encode(address)).put("amount", amount);
    }
}
