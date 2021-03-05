package org.wolkenproject.network;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class AddressList extends Message {
    public AddressList(Set<NetAddress> list) {
        super(version, Flags.Notify);
    }

    @Override
    public void executePayload(Server server, Node node) {
        node.incrementReceivedAddresses();
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {

    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {

    }

    @Override
    public <Type> Type getPayload() {
        return null;
    }

    @Override
    public ResponseMetadata getResponseMetadata() {
        return null;
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
