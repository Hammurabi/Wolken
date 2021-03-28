package org.wolkenproject.core;

import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RawAddress {
    private final byte address[];

    public RawAddress(byte address[]) {
        this.address = address;
    }

    public static void write(RawAddress[] addresses, OutputStream stream) throws IOException {
        VarInt.writeCompactUInt32(addresses.length, false, stream);
        for (RawAddress address : addresses) {
            stream.write(address.getAddress());
        }
    }

    public static RawAddress[] readAll(InputStream stream) throws IOException {
        int num = VarInt.readCompactUInt32(false, stream);
        RawAddress addresses[] = new RawAddress[num];
        for (int i = 0; i < addresses.length; i ++) {
            byte raw[] = new byte[20];
            SerializableI.checkFullyRead(stream.read(raw), raw.length);
            addresses[i] = new RawAddress(raw);
        }

        return addresses;
    }

    public byte[] getAddress() {
        return address;
    }
}
