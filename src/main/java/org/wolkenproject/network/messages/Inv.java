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
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

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

    public Inv(int type, Collection<byte[]> list) {
        this(Context.getInstance().getNetworkParameters().getVersion(), type, list);
    }

    public Inv(int version, int type, byte[] singleHash) {
        this(version, type, asList(singleHash));
    }

    public Inv(int version, int type, Collection<byte[]> list) {
        super(version, Flags.Notify);
        this.list = new LinkedHashSet<>(list);

        switch (type)
        {
            case Type.Block:
                requiredLength = Block.UniqueIdentifierLength;
                break;
            case Type.Transaction:
                requiredLength = Transaction.UniqueIdentifierLength;
                break;
        }
    }

    public static Set<byte[]> convert(Collection<Transaction> transactions)
    {
        Set<byte[]> result = new HashSet<>();
        for (Transaction transaction : transactions)
        {
            result.add(transaction.getHash());
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
                CheckedResponse message = node.getResponse(new RequestTransactions(Context.getInstance().getNetworkParameters().getVersion(), newTransactions),
                        Context.getInstance().getNetworkParameters().getMessageTimeout());

                if (message == null) {
                    node.increaseErrors(4);
                    return;
                }

                if (message.noErrors()) {
                    Set<Transaction> transactions = message.getMessage().getPayload();
                    transactions.removeIf(transaction -> !transaction.shallowVerify());
                    Context.getInstance().getTransactionPool().add(transactions);

                    Set<byte[]> validTransactions = new LinkedHashSet<>();
                    for (Transaction transaction : transactions) {
                        validTransactions.add(transaction.getHash());
                    }

                    Inv inv = new Inv(Context.getInstance().getNetworkParameters().getVersion(), Type.Transaction, newTransactions);
                    Context.getInstance().getServer().broadcast(inv, node);
                } else {
                    node.increaseErrors(4);
                }
            } catch (WolkenTimeoutException e) {
                node.increaseErrors(4);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
        VarInt.writeCompactUInt32(type, false, stream);
        VarInt.writeCompactUInt32(list.size(), false, stream);

        for (byte[] id : list) {
            stream.write(id);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
        type = VarInt.readCompactUInt32(false, stream);
        int length = VarInt.readCompactUInt32(false, stream);

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

    private static Collection<byte[]> asList(byte[] singleHash) {
        List<byte[]> list = new ArrayList<>();
        list.add(singleHash);

        return list;
    }
}
