package org.wokenproject.network.messages;

import org.wokenproject.core.Context;
import org.wokenproject.core.TransactionI;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.network.Message;
import org.wokenproject.network.Node;
import org.wokenproject.network.Server;
import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class TransactionList extends Message {
    private Set<TransactionI> transactions;

    public TransactionList(int version, Collection<TransactionI> transactions) {
        super(version, Flags.Response);
        this.transactions = new LinkedHashSet<>(transactions);
    }

    @Override
    public void executePayload(Server server, Node node) {
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException {
        Utils.writeInt(transactions.size(), stream);
        for (TransactionI transaction : transactions)
        {
            transaction.write(stream);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException {
        byte buffer[] = new byte[4];
        stream.read(buffer);
        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i ++)
        {
            try {
                TransactionI transaction = Context.getInstance().getSerialFactory().fromStream(Context.getInstance().getSerialFactory().getSerialNumber(TransactionI.class), stream);
                transactions.add(transaction);
            } catch (WolkenException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new TransactionList(getVersion(), transactions);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(TransactionList.class);
    }
}
