package org.wolkenproject.core.events;

import org.json.JSONObject;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.utils.Utils;

public class RegisterAliasEvent extends Event {
    private byte    address[];
    private long    alias;

    public RegisterAliasEvent(byte[] address, long alias) {
        super();
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().registerAlias(address, alias);
    }

    @Override
    public void undo() {
        Context.getInstance().getDatabase().removeAlias(alias);
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Register Alias".getBytes(), address, Utils.takeApartLong(alias));
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("event", this.getClass().getName()).put("address", Base58.encode(address)).put("alias", alias);
    }

    public long getAlias() {
        return alias;
    }
}
