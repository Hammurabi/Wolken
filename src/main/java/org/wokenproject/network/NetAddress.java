package org.wokenproject.network;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class NetAddress extends SerializableI implements Serializable {
    private static final long serialVersionUID = 3738771433856794716L;
    private final InetAddress   address;
    private final int           port;
    private double              spamAverage;

    public NetAddress(InetAddress address, int port)
    {
        this.address    = address;
        this.port       = port;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetAddress that = (NetAddress) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return address.toString().hashCode();
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        byte bytes[] = address.getAddress();
        stream.write(bytes.length);
        stream.write(bytes);
        Utils.writeUnsignedInt16(port, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        try {
            return (Type) new NetAddress(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            throw new WolkenException(e);
        }
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(NetAddress.class);
    }
}
