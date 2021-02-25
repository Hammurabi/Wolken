package org.wokenproject.network.messages;

import org.wokenproject.network.Message;
import org.wokenproject.network.Node;
import org.wokenproject.network.Server;
import org.wokenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public class List<T extends SerializableI> extends Message {
    public List(int version, Collection<T> list) {
        super(version, Flags.NOTIFY, list.size());
    }

    @Override
    public void executePayload(Server server, Node node) {

    }

    @Override
    public void write(OutputStream stream) throws IOException {

    }

    @Override
    public void read(InputStream stream) throws IOException {

    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) {
        return null;
    }
}
