package org.wokenproject.network;

import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VersionInformation extends SerializableI {
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
    }

    @Override
    public void write(OutputStream stream) throws IOException {
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
