package org.wolkenproject.core.events;

import org.json.JSONObject;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;
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
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Register Alias".getBytes(), address, Utils.takeApartLong(alias));
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    public long getAlias() {
        return alias;
    }
}
