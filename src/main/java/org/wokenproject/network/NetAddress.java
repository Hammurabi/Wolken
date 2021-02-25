package org.wokenproject.network;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

public class NetAddress implements Serializable {
    private static final long serialVersionUID = 3738771433856794716L;
    private final InetAddress address;
    private final short       port;

    public NetAddress(InetAddress address, short port)
    {
        this.address    = address;
        this.port       = port;
    }

    public InetAddress getAddress()
    {
        return address;
    }

    public short getPort()
    {
        return port;
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
