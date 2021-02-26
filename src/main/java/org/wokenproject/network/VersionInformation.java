package org.wokenproject.network;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.network.messages.VersionMessage;
import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VersionInformation extends SerializableI {
    private int version;
    private long services;
    private long timestamp;
    private NetAddress sender;
    private NetAddress receiver;
    private int blockHeight;
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
        this.services = Utils.makeLong(buffer);
        this.timestamp = Utils.makeLong(buffer);
        sender = Context.getInstance().getSerialFactory().fromStream(Context.getInstance().getSerialFactory().getSerialNumber(NetAddress.class), stream);
        receiver = Context.getInstance().getSerialFactory().fromStream(Context.getInstance().getSerialFactory().getSerialNumber(NetAddress.class), stream);
        this.blockHeight = Utils.makeInt(buffer);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return null;
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(VersionInformation.class);
    }
}
