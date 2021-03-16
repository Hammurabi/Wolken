package org.wolkenproject.network;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class VersionInformation extends SerializableI {
    public static final class Flags{
        public static final long
            AllServices = 0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L
        ;
    }

    private int version;
    private long services;
    private long timestamp;
    private NetAddress sender;
    private NetAddress receiver;
    private int blockHeight;
    private byte nonce[];

    public VersionInformation() throws UnknownHostException {
        this(0, 0, 0, new NetAddress(InetAddress.getLocalHost(), 0, 0), new NetAddress(InetAddress.getLocalHost(), 0, 0), 0, new byte[20]);
    }
    /**
     * @param version       client version
     * @param services      bitfield of services provided by this client
     * @param timestamp     unix timestamp of message creation time
     * @param sender        sender address
     * @param receiver      received address
     * @param blockHeight   current block height
     */
    public VersionInformation(int version, long services, long timestamp, NetAddress sender, NetAddress receiver, int blockHeight, byte nonce[])
    {
        this.version = version;
        this.services = services;
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
        this.blockHeight = blockHeight;
        this.nonce = nonce;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        Utils.writeInt(version, stream);
        Utils.writeLong(services, stream);
        Utils.writeLong(timestamp, stream);
        sender.write(stream);
        receiver.write(stream);
        Utils.writeInt(blockHeight, stream);
        stream.write(nonce);
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
        checkFullyRead(stream.read(nonce), 20);
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

    public boolean isSelfConnection(byte nonce[]) {
        return Arrays.equals(nonce, this.nonce);
    }
}
