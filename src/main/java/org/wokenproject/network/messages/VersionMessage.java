package org.wokenproject.network.messages;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.network.*;
import org.wokenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class VersionMessage extends Message {
    private VersionInformation versionInformation;

    public VersionMessage(int version, VersionInformation versionInformation) {
        super(version, Flags.Notify);
        this.versionInformation = versionInformation;
    }

    @Override
    public void executePayload(Server server, Node node) {
        node.setVersionInfo(this);
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
        versionInformation.write(stream);
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
        versionInformation.read(stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        try {
            return (Type) new VersionMessage(0, new VersionInformation(0, 0, 0, new NetAddress(InetAddress.getLocalHost(), 0), new NetAddress(InetAddress.getLocalHost(), 0), 0 ));
        } catch (UnknownHostException e) {
            throw new WolkenException(e);
        }
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(VersionMessage.class);
    }

    public VersionInformation getVersionInformation()
    {
        return versionInformation;
    }
}