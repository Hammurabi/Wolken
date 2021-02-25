package org.wokenproject.core;

import org.wokenproject.network.NetAddress;
import org.wokenproject.utils.FileService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

public class IpAddressList {
    private Set<NetAddress> addresses;

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
    }

    public void removeAddress(NetAddress address)
    {
    }

    public Set<NetAddress> getAddresses()
    {
        return addresses;
    }
}
