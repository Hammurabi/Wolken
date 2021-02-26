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
import java.util.LinkedHashSet;
import java.util.Set;

public class Inv extends Message {
    public static class Type
    {
        public static final int
        None = 0,
        Block = 1,
        Transaction = 2;
    }

    private Set<byte[]> list;
    private int         type;

    public Inv(int version, int type, Collection<byte[]> list) throws WolkenException {
        super(version, Flags.Notify);
        this.list = new LinkedHashSet<>(list);
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
        newTransactions = Context.getInstance().getDatabase().getNonDuplicateTransactions(newTransactions);

        if (newTransactions.isEmpty())
        {
            return;
        }

        node.sendMessage(new RequestTransactions(Context.getInstance().getNetworkParameters().getVersion(), newTransactions));
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException {
    }

    @Override
    public void readContents(InputStream stream) throws IOException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Inv(getVersion(), new LinkedHashSet<>());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Inv.class);
    }
}
