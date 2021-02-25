package org.wokenproject.network.messages;

import org.wokenproject.core.Context;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionList extends Message {
    private Set<byte[]> list;

    public TransactionList(int version, Collection<byte[]> list) throws WolkenException {
        super(version, Flags.NOTIFY, list.size());
        this.list = new HashSet<>(list);
        for (byte[] uid : this.list)
        {
            if (uid == null)
            {
                throw new WolkenException("provided hash is null.");
            }

            if (uid.length != TransactionI.UniqueIdentifierLength)
            {
                throw new WolkenException("provided hash is incompatible.");
            }
        }
    }

    public static Set<byte[]> convert(Collection<TransactionI> transactions)
    {
        Set<byte[]> result = new HashSet<>();
        for (TransactionI transaction : transactions)
        {
            result.add(transaction.getTransactionID());
        }

        return result;
    }

    @Override
    public void executePayload(Server server, Node node) {
        Set<byte[]> newTransactions = Context.getInstance().getTransactionPool().getNonDuplicateTransactions(list);

        if (newTransactions.isEmpty())
        {
            return;
        }

        node.sendMessage(new RequestTransactions(Context.getInstance().getNetworkParameters().getVersion(), newTransactions));
    }

    @Override
    public void write(OutputStream stream) throws IOException {
    }

    @Override
    public void read(InputStream stream) throws IOException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new TransactionList(0, new HashSet<>());
    }

    @Override
    public byte[] getUniqueMessageIdentifier() {
        return new byte[0];
    }
}
