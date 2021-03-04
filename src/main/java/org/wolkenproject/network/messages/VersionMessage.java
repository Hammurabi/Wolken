package org.wolkenproject.network.messages;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.*;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class VersionMessage extends Message {
    private VersionInformation versionInformation;

    public VersionMessage() throws UnknownHostException {
        this(0, new VersionInformation(0, 0, 0, new NetAddress(InetAddress.getLocalHost(), 0, 0), new NetAddress(InetAddress.getLocalHost(), 0, 0), 0 ));
    }

    public VersionMessage(int version, VersionInformation versionInformation) {
        super(version, Flags.Notify);
        this.versionInformation = versionInformation;
    }

    @Override
    public void executePayload(Server server, Node node) {
        node.setVersionInfo(versionInformation);

        if (!Context.getInstance().getNetworkParameters().isVersionCompatible(versionInformation.getVersion(), Context.getInstance().getNetworkParameters().getVersion())) {
            // send bye message.
        } else {
            // send verack
            node.sendMessage(new VerackMessage(Context.getInstance().getNetworkParameters().getVersion(), new VersionInformation(
                    Context.getInstance().getNetworkParameters().getVersion(),
                    Context.getInstance().getNetworkParameters().getServices(),
                    System.currentTimeMillis(),
                    server.getNetAddress(),
                    node.getNetAddress(),
                    0
            )));
        }
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
    public <Type> Type getPayload() {
        return (Type) versionInformation;
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        try {
            return (Type) new VersionMessage();
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