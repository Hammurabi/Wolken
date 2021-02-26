package org.wokenproject.network.messages;

import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.network.Message;
import org.wokenproject.network.NetAddress;
import org.wokenproject.network.Node;
import org.wokenproject.network.Server;
import org.wokenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VersionMessage extends Message {
    public VersionMessage(int version, long services, long timestamp, NetAddress sender, NetAddress receiver, int blockHeight) {
        super(version, Flags.Notify);
    }

    @Override
    public void executePayload(Server server, Node node) {
        node.setVersionInfo(this);
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException {

    }

    @Override
    public void readContents(InputStream stream) throws IOException {

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