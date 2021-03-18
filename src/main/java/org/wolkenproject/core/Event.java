package org.wolkenproject.core;

import org.wolkenproject.utils.HashUtil;

public abstract class Event {
    public abstract void apply();
    public abstract void undo();
    public abstract byte[] getEventBytes();
    public byte[] eventId() {
        return HashUtil.sha256d(getEventBytes());
    }
}
