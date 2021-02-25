package org.wokenproject.core;

import org.wokenproject.network.NetAddress;
import org.wokenproject.utils.FileService;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class IpAddressList {
    private Set<NetAddress> addresses;
    private FileService     service;

    public IpAddressList(FileService service)
    {
        if (service.exists())
        {
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(service.file()));
                this.addresses = (Set<NetAddress>) stream.readObject();
                stream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
        {
            this.addresses  = new HashSet<>();
        }

        this.service = service;
    }

    public void addAddress(NetAddress address)
    {
        addresses.add(address);
    }

    public void removeAddress(NetAddress address)
    {
        addresses.remove(address);
    }

    public Set<NetAddress> getAddresses()
    {
        return addresses;
    }

    public void save() throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(service.file()));
        stream.writeObject(addresses);
        stream.flush();
        stream.close();
    }
}
