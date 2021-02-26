package org.wokenproject.network;

import org.wokenproject.exceptions.WolkenException;
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
        Utils.writeInt(blockHeight);
    }

    @Override
    public void read(InputStream stream) throws IOException {
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
