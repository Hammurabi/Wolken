package org.wokenproject.network;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class VersionInformation extends SerializableI {
    private int version;
    private long services;
    private long timestamp;
    private NetAddress sender;
    private NetAddress receiver;
    private int blockHeight;

    public VersionInformation() throws UnknownHostException {
        this(0, 0, 0, new NetAddress(InetAddress.getLocalHost(), 0), new NetAddress(InetAddress.getLocalHost(), 0), 0 );
    }
    /**
     * @param version       client version
     * @param services      bitfield of services provided by this client
     * @param timestamp     unix timestamp of message creation time
     * @param sender        sender address
     * @param receiver      received address
     * @param blockHeight   current block height
     */
    public VersionInformation(int version, long services, long timestamp, NetAddress sender, NetAddress receiver, int blockHeight)
    {
        this.version = version;
        this.services = services;
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
        this.blockHeight = blockHeight;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        Utils.writeInt(version, stream);
        Utils.writeLong(services, stream);
        Utils.writeLong(timestamp, stream);
        sender.write(stream);
        receiver.write(stream);
        Utils.writeInt(blockHeight, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        byte buffer[] = new byte[8];
        stream.read(buffer, 0, 4);
        this.version = Utils.makeInt(buffer);
        stream.read(buffer);
        this.services = Utils.makeLong(buffer);
        stream.read(buffer);
        this.timestamp = Utils.makeLong(buffer);
        sender = Context.getInstance().getSerialFactory().fromStream(Context.getInstance().getSerialFactory().getSerialNumber(NetAddress.class), stream);
        receiver = Context.getInstance().getSerialFactory().fromStream(Context.getInstance().getSerialFactory().getSerialNumber(NetAddress.class), stream);
        stream.read(buffer, 0, 4);
        this.blockHeight = Utils.makeInt(buffer);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        try {
            return (Type) new VersionInformation();
        } catch (UnknownHostException e) {
            throw new WolkenException(e);
        }
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(VersionInformation.class);
    }

    public int getVersion()
    {
        return version;
    }

    public long getServices()
    {
        return services;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public NetAddress getSender()
    {
        return sender;
    }

    public NetAddress getReceiver()
    {
        return receiver;
    }

    public int getBlockHeight()
    {
        return blockHeight;
    }
}
