package org.wolkenproject.network;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

public class AddressList extends Message {
    private Set<NetAddress> addresses;

    public AddressList(int version, Set<NetAddress> list) {
        super(version, Flags.Notify);
        this.addresses = list;
    }

    @Override
    public void executePayload(Server server, Node node) {
        node.incrementReceivedAddresses();

        Context.getInstance().getIpAddressList().add(addresses);
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(addresses.size(), stream);
        for (NetAddress address : addresses) {
            address.write(stream);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
        byte buffer[] = new byte[4];
        stream.read(buffer);
        int length = Utils.makeInt(buffer);
        for (int i = 0; i < length; i ++) {
            addresses.add(Context.getInstance().getSerialFactory().fromStream(NetAddress.class, stream));
        }
    }

    @Override
    public <Type> Type getPayload() {
        return (Type) addresses;
    }

    @Override
    public ResponseMetadata getResponseMetadata() {
        return null;
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new AddressList(0, new LinkedHashSet<>());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(AddressList.class);
    }
}
