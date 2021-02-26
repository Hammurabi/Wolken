package org.wokenproject.network.messages;

import org.wokenproject.core.TransactionI;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.network.Message;
import org.wokenproject.network.Node;
import org.wokenproject.network.Server;
import org.wokenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;

public class TransactionList extends Message {
    public TransactionList(int version, Collection<TransactionI> transactions) {
        super(version, Flags.RESPONSE, transactions.size());
    }

    @Override
    public void executePayload(Server server, Node node) {
    }

    @Override
    public byte[] getUniqueMessageIdentifier() {
        return new byte[0];
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
}
