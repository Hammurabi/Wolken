package org.wokenproject.network.messages;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.network.Message;
import org.wokenproject.network.Node;
import org.wokenproject.network.Server;
import org.wokenproject.network.VersionInformation;
import org.wokenproject.serialization.SerializableI;

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

        node.sendMessage(new RequestInv());
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
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
