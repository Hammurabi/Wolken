package org.wolkenproject.network.messages;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.*;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class VersionMessage extends Message {
    private VersionInformation versionInformation;

    public VersionMessage() throws UnknownHostException {
        this(0, new VersionInformation(0, 0, 0, new NetAddress(InetAddress.getLocalHost(), 0, 0), new NetAddress(InetAddress.getLocalHost(), 0, 0), 0, new byte[20]));
    }

    public VersionMessage(int version, VersionInformation versionInformation) {
        super(version, Flags.Notify);
        this.versionInformation = versionInformation;
    }

    @Override
    public void executePayload(Server server, Node node) {
        node.setVersionInfo(versionInformation);
        Logger.alert("received version info ${i}", versionInformation);

        if (!Context.getInstance().getNetworkParameters().isVersionCompatible(versionInformation.getVersion(), Context.getInstance().getNetworkParameters().getVersion())) {
            // send bye message.
            Logger.alert("terminating connection.. (incompatible versions)");
            node.sendMessage(new CheckoutMessage(CheckoutMessage.Reason.SelfConnect));
        } else if (versionInformation.isSelfConnection(server.getNonce())) {
            // this is a self connection, we must terminate it
            Logger.alert("terminating self connection..");
            node.sendMessage(new CheckoutMessage(CheckoutMessage.Reason.SelfConnect));
        } else {
            Logger.alert("sending verack..");
            // send verack
            node.sendMessage(new VerackMessage(Context.getInstance().getNetworkParameters().getVersion(), new VersionInformation(
                    Context.getInstance().getNetworkParameters().getVersion(),
                    Context.getInstance().getNetworkParameters().getServices(),
                    System.currentTimeMillis(),
                    server.getNetAddress(),
                    node.getNetAddress(),
                    Context.getInstance().getBlockChain().getHeight(),
                    server.getNonce()
            )));

            Context.getInstance().getIpAddressList().send(node);
            node.sendMessage(new RequestInv(Context.getInstance().getNetworkParameters().getVersion()));
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
    public ResponseMetadata getResponseMetadata() {
        return null;
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