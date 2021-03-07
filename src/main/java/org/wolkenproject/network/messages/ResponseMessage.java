package org.wolkenproject.network.messages;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.ResponseMetadata;
import org.wolkenproject.network.Server;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ResponseMessage extends Message {
    private byte[] requester;

    public ResponseMessage(int version, byte uniqueMessageIdentifier[]) {
        super(version, Flags.Response);
        this.requester = uniqueMessageIdentifier;
    }

    @Override
    public void executePayload(Server server, Node node) {
        node.receiveResponse(this, requester);
    }

    @Override
    public void writeHeader(OutputStream stream) throws IOException, WolkenException {
        super.writeHeader(stream);
        stream.write(requester);
    }

    @Override
    public void readHeader(InputStream stream) throws IOException, WolkenException {
        super.readHeader(stream);
        stream.read(requester);
    }

    protected byte[] getRequester() {
        return requester;
    }

    @Override
    public ResponseMetadata getResponseMetadata() {
        return null;
    }
}
