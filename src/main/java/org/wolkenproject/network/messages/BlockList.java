package org.wolkenproject.network.messages;

import org.wolkenproject.core.Context;
import org.wolkenproject.core.TransactionI;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlockList extends ResponseMessage {
    private Set<TransactionI>   transactions;

    public BlockList(int version, Collection<TransactionI> transactions, byte[] uniqueMessageIdentifier) {
        super(version, uniqueMessageIdentifier);
        this.transactions   = new LinkedHashSet<>(transactions);
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(transactions.size(), stream);
        for (TransactionI transaction : transactions)
        {
            transaction.write(stream);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
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
        return (Type) new BlockList(getVersion(), transactions, getUniqueMessageIdentifier());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(BlockList.class);
    }
}
