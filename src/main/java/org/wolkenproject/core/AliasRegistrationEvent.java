package org.wolkenproject.core;

public class AliasRegistrationEvent extends Event {
    public AliasRegistrationEvent(byte[] raw) {
        super();
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
