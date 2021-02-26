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
import java.util.LinkedHashSet;
import java.util.Set;

public class RequestTransactions extends Message {
    private Set<byte[]> transactions;

    public RequestTransactions(int version, Collection<byte[]> transactions) {
        super(version, Flags.Request);
        this.transactions = new LinkedHashSet<>(transactions);
    }

    @Override
    public void executePayload(Server server, Node node) {
        Set<TransactionI> transactions = new LinkedHashSet<>();
        for (byte[] txid : this.transactions)
        {
            TransactionI transaction = Context.getInstance().getTransactionPool().getTransaction(txid);
            if (transaction == null)
            {
                transaction = Context.getInstance().getDatabase().getTransaction(txid);
            }

            if (transaction != null)
            {
                transactions.add(transaction);
            }
        }

        node.sendMessage(new TransactionList(Context.getInstance().getNetworkParameters().getVersion(), transactions));
    }

    @Override
    public byte[] getContents() {
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
        return (Type) new RequestTransactions(getVersion(), transactions);
    }
}
