package org.wolkenproject.core.events;

import org.json.JSONObject;
import org.wolkenproject.core.Event;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DestroyContractEvent extends Event {
    public DestroyContractEvent(byte[] contractId) {
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

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return null;
    }

    @Override
    public int getSerialNumber() {
        return 0;
    }
}
