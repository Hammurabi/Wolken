package org.wolkenproject.core.events;

import org.json.JSONObject;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.utils.Utils;

public class NewAccountEvent extends Event {
    private byte    address[];

    public NewAccountEvent(byte[] address) {
        super();
        this.address    = address;
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().newAccount(address);
    }

    @Override
    public void undo() {
        Context.getInstance().getDatabase().rmvAccount(address);
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Account Registration".getBytes(), address);
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("event", this.getClass().getName()).put("address", Base58.encode(address));
    }

    public byte[] getAddress() {
        return address;
    }
}
