package org.wokenproject.network;

import org.wokenproject.network.NetAddress;
import org.wokenproject.utils.FileService;

import java.io.*;
import java.net.InetAddress;
import java.util.*;

public class IpAddressList {
    private Map<byte[], NetAddress> addresses;
    private FileService             service;

    public IpAddressList(FileService service)
    {
        if (service.exists())
        {
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(service.file()));
                this.addresses = (Map<byte[], NetAddress>) stream.readObject();
                stream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
        {
            this.addresses  = new HashMap<>();
        }

        this.service = service;
    }

    public void addAddress(NetAddress address)
    {
        addresses.put(address.getAddress().getAddress(), address);
    }

    public void removeAddress(NetAddress address)
    {
        addresses.remove(address);
    }

    public Queue<NetAddress> getAddresses()
    {
        return new PriorityQueue(addresses.values());
    }

    public void save() throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(service.file()));
        stream.writeObject(addresses);
        stream.flush();
        stream.close();
    }

    public NetAddress getAddress(InetAddress inetAddress) {
        return addresses.get(inetAddress.getAddress());
    }
}
