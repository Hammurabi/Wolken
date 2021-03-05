package org.wolkenproject.network.messages;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.ResponseMetadata;
import org.wolkenproject.network.Server;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RequestCommonAncestorChain extends Message {
    @Override
    public void executePayload(Server server, Node node) {
        
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
