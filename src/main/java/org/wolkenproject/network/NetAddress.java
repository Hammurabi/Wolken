package org.wolkenproject.network;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class NetAddress extends SerializableI implements Serializable {
    private static final long serialVersionUID = 3738771433856794716L;
    private InetAddress address;
    private int         port;
    private long        services;
    private double      spamAverage;

    public NetAddress(InetAddress address, int port, long services)
    {
        this.address    = address;
        this.port       = port;
        this.services   = services;
    }

    public void setSpamAverage(double spamAverage)
    {
        // slowly dilute old values
        this.spamAverage = (this.spamAverage + spamAverage) / 2.;
    }

    public InetAddress getAddress()
    {
        return address;
    }

    public int getPort()
    {
        return port;
    }

    public double getSpamAverage()
    {
        return spamAverage;
    }

    public long getServices()
    {
        return services;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetAddress that = (NetAddress) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return address.getAddress().hashCode();
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        byte bytes[] = address.getAddress();
        stream.write(bytes.length);
        stream.write(bytes);
        Utils.writeUnsignedInt16(port, stream);
        Utils.writeLong(services, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException {
        int length = stream.read();
        byte bytes[] = new byte[length];
        stream.read(bytes);
        address = InetAddress.getByAddress(bytes);
        port    = Utils.makeInt((byte) 0, (byte) 0, (byte) stream.read(), (byte) stream.read());
        stream.read(bytes, 0, 8);
        services= Utils.makeLong(bytes);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        try {
            return (Type) new NetAddress(InetAddress.getLocalHost(), port, 0);
        } catch (UnknownHostException e) {
            throw new WolkenException(e);
        }
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(NetAddress.class);
    }

    public void setServices(long services) {
        this.services = services;
    }
}
