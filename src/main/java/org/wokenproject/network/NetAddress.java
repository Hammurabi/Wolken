package org.wokenproject.network;

import java.io.Serializable;
import java.net.InetAddress;

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
}
