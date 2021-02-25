package org.wokenproject.network;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

public class NetAddress implements Serializable {
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
}
