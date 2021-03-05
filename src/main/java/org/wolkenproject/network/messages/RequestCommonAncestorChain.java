package org.wolkenproject.network.messages;

import org.wolkenproject.core.BlockHeader;
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
import java.util.Set;

public class RequestCommonAncestorChain extends Message {
    private Ancestors           ancestors;

    public RequestCommonAncestorChain(int version, Ancestors ancestors) {
        super(version, Flags.Request);
        this.ancestors = ancestors;
    }

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
        return (Type) headers;
    }

    @Override
    public ResponseMetadata getResponseMetadata() {
        return (msg)->{
            return 0;
        };
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RequestCommonAncestorChain(0, new Ancestors());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(RequestCommonAncestorChain.class);
    }
}
