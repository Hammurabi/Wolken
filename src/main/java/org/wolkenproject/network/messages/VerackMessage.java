package org.wolkenproject.network.messages;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.*;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;

public class VerackMessage extends Message {
    private VersionInformation versionInformation;

    public VerackMessage() throws UnknownHostException {
        this(0, new VersionInformation());
    }

    public VerackMessage(int version, VersionInformation versionInformation) {
        super(version, Flags.Notify);
    }

    @Override
    public void executePayload(Server server, Node node) {
        node.setVersionInfo(versionInformation);

        node.sendMessage(new RequestInv(Context.getInstance().getNetworkParameters().getVersion()));
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type> Type getPayload() {
        return (Type) versionInformation;
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
        return Context.getInstance().getSerialFactory().getSerialNumber(VerackMessage.class);
    }
}
