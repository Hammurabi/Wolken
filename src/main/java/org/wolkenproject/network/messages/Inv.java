package org.wolkenproject.network.messages;

import org.wolkenproject.core.Block;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.exceptions.WolkenTimeoutException;
import org.wolkenproject.network.*;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Tuple;
import org.wolkenproject.utils.Utils;

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

    public Inv(int type, Collection<byte[]> list) throws WolkenException {
        this(Context.getInstance().getNetworkParameters().getVersion(), type, list);
    }

    public Inv(int version, int type, Collection<byte[]> list) throws WolkenException {
        super(version, Flags.Notify);
        this.list = new LinkedHashSet<>(list);
        int requiredLength = 0;

        switch (type)
        {
            case Type.Block:
                requiredLength = Block.UniqueIdentifierLength;
                break;
            case Type.Transaction:
                requiredLength = Transaction.UniqueIdentifierLength;
                break;
        }

        for (byte[] uid : this.list)
        {
            if (uid == null)
            {
                throw new WolkenException("provided hash is null.");
            }

            if (uid.length != requiredLength)
            {
                throw new WolkenException("provided hash is incompatible.");
            }
        }
    }

    public static Set<byte[]> convert(Collection<Transaction> transactions)
    {
        Set<byte[]> result = new HashSet<>();
        for (Transaction transaction : transactions)
        {
            result.add(transaction.getTransactionID());
        }

        return result;
    }

    @Override
    public void executePayload(Server server, Node node) {
        if (type == Type.Block)
        {
            Set<byte[]> newBlocks = new LinkedHashSet<>();

            for (byte[] hash : list) {
                if (!Context.getInstance().getDatabase().checkBlockExists(hash) && !Context.getInstance().getBlockChain().contains(hash)) {
                    newBlocks.add(hash);
                }
            }

            // request the blocks
            try {
                CheckedResponse message = node.getResponse(new RequestBlocks(Context.getInstance().getNetworkParameters().getVersion(), newBlocks), Context.getInstance().getNetworkParameters().getMessageTimeout());
                if (message != null) {
                    if (message.noErrors()) {
                        Set<BlockIndex> blocks = message.getMessage().getPayload();
                        Context.getInstance().getBlockChain().suggest(blocks);

                        Inv inv = new Inv(Context.getInstance().getNetworkParameters().getVersion(), Type.Block, newBlocks);
                        Set<Node> connected = Context.getInstance().getServer().getConnectedNodes();
                        connected.remove(node);

                        for (Node n : connected) {
                            n.sendMessage(inv);
                        }
                    }
                }
            } catch (WolkenTimeoutException e) {
                e.printStackTrace();
            } catch (WolkenException e) {
                e.printStackTrace();
            }
        }
        else if (type == Type.Transaction)
        {
            Set<byte[]> newTransactions = Context.getInstance().getTransactionPool().getNonDuplicateTransactions(list);

            if (newTransactions.isEmpty())
            {
                return;
            }

            // request the transactions
            try {
                CheckedResponse message = node.getResponse(new RequestTransactions(Context.getInstance().getNetworkParameters().getVersion(), newTransactions), Context.getInstance().getNetworkParameters().getMessageTimeout());
                if (message != null) {
                    if (message.noErrors()) {
                        Set<Transaction> transactions = message.getMessage().getPayload();
                        Context.getInstance().getTransactionPool().add(transactions);

                        Inv inv = new Inv(Context.getInstance().getNetworkParameters().getVersion(), Type.Transaction, newTransactions);
                        Set<Node> connected = Context.getInstance().getServer().getConnectedNodes();
                        connected.remove(node);

                        for (Node n : connected) {
                            n.sendMessage(inv);
                        }
                    }
                }
            } catch (WolkenTimeoutException e) {
                e.printStackTrace();
            } catch (WolkenException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(type, stream);
        Utils.writeInt(list.size(), stream);

        for (byte[] id : list)
        {
            stream.write(id);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
        byte buffer[] = new byte[4];
        stream.read(buffer);
        type = Utils.makeInt(buffer);
        stream.read(buffer);
        int length = Utils.makeInt(buffer);

        int requiredLength = 0;

        switch (type)
        {
            case Type.Block:
                requiredLength = Block.UniqueIdentifierLength;
                break;
            case Type.Transaction:
                requiredLength = Transaction.UniqueIdentifierLength;
                break;
        }

        byte id[] = new byte[requiredLength];

        for (int i = 0; i < length; i ++)
        {
            stream.read(id);
            list.add(id);
        }
    }

    @Override
    public <Type> Type getPayload() {
        return (Type) new Tuple(list, type);
    }

    @Override
    public ResponseMetadata getResponseMetadata() {
        return null;
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Inv(getVersion(), 0, new LinkedHashSet<>());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Inv.class);
    }
}
