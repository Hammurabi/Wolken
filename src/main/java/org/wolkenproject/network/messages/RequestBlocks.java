package org.wolkenproject.network.messages;

import org.wolkenproject.core.Block;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.TransactionI;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.Server;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class RequestBlocks extends Message {
    private Set<byte[]> blocks;

    public RequestBlocks(int version, Collection<byte[]> blocks) {
        super(version, Flags.Request);
        this.blocks = new LinkedHashSet<>(blocks);
    }

    @Override
    public void executePayload(Server server, Node node) {
        Set<Block> blocks = new LinkedHashSet<>();
        for (byte[] hash : this.blocks)
        {
            Block block = Context.getInstance().getDatabase().findBlock(hash);
            if (transaction == null)
            {
                transaction = Context.getInstance().getDatabase().getTransaction(txid);
            }

            if (transaction != null)
            {
                transactions.add(transaction);
            }
        }

        node.sendMessage(new TransactionList(Context.getInstance().getNetworkParameters().getVersion(), transactions, getUniqueMessageIdentifier()));
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException {
        Utils.writeInt(blocks.size(), stream);
        for (byte[] txid : blocks)
        {
            stream.write(txid);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException {
        byte buffer[] = new byte[4];
        stream.read(buffer);

        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i ++)
        {
            byte txid[] = new byte[TransactionI.UniqueIdentifierLength];
            stream.read(txid);

            blocks.add(txid);
        }
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RequestBlocks(getVersion(), blocks);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(RequestBlocks.class);
    }
}
