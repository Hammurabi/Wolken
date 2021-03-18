package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.ChainMath;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.*;

public class Block extends BlockHeader implements Iterable<Transaction> {
    private static BigInteger LargestHash = BigInteger.ONE.shiftLeft(256);
    public static int UniqueIdentifierLength = 32;
    private Set<Transaction>   transactions;

    public Block() {
        this(new byte[32], 0);
    }

    public Block(byte previousHash[], int bits)
    {
        super(Context.getInstance().getNetworkParameters().getVersion(), Utils.timestampInSeconds(), previousHash, new byte[32], bits, 0);
        transactions = new LinkedHashSet<>();
    }

    public final int calculateSize() {
        int transactionLength = 0;
        for (Transaction transaction : transactions) {
            transactionLength += transaction.calculateSize();
        }

        return BlockHeader.Size + VarInt.sizeOfCompactUin32(transactions.size(), false) + transactionLength;
    }

    /*
        returns a new block header
     */
    public final BlockHeader getBlockHeader() {
        return new BlockHeader(getVersion(), getTimestamp(), getParentHash(), getMerkleRoot(), getBits(), getNonce());
    }

    // executes transctions and returns an event list
    public BlockStateChangeResult getStateChange(int blockHeight) {
        List<Event> events = new ArrayList<>();
        Queue<byte[]> txids = new LinkedList<>();
        Queue<byte[]> txeids = new LinkedList<>();

        long accumulatedFees = 0L;

        for (Transaction transaction : transactions) {
            accumulatedFees += transaction.getTransactionFee();
        }

        for (Transaction transaction : transactions) {
            List<Event> transactionEvents = transaction.getStateChange(this, blockHeight, accumulatedFees);
            events.addAll(transactionEvents);
            txids.add(transaction.getTransactionID());
            transactionEvents.forEach(event -> txeids.add(event.eventId()));
        }

        return new BlockStateChangeResult(txids, txeids, events);
    }

    // call transaction.verify()
    // this does not mean that transactions are VALID
    private boolean verifyTransactions() throws WolkenException {
        for (Transaction transaction : transactions) {
            if (!transaction.shallowVerify()) {
                return false;
            }
        }

        return true;
    }

    public void build(int blockHeight) {
        // set the combined merkle root
        setMerkleRoot(getStateChange(blockHeight).getMerkleRoot());
    }

    public boolean verify(int blockHeight) throws WolkenException {
        if (!verifyTransactions()) return false;

        long accumulatedFees = 0L;

        for (Transaction transaction : transactions) {
            accumulatedFees += transaction.getTransactionFee();
        }

        if (!verifyTransactions(this, blockHeight, accumulatedFees)) return false;
        if (!Utils.equals(getStateChange(blockHeight).getMerkleRoot(), getMerkleRoot())) return false;

        return true;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        super.write(stream);
        Utils.writeInt(transactions.size(), stream);
        for (Transaction transaction : transactions)
        {
            // use serialize here to write transaction serial id
            transaction.serialize(stream);
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        super.read(stream);
        byte buffer[] = new byte[4];
        stream.read(buffer);
        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i ++)
        {
            transactions.add(Context.getInstance().getSerialFactory().fromStream(stream));
        }
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Block();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Block.class);
    }

    public Transaction getCoinbase()
    {
        Iterator<Transaction> transactions = this.transactions.iterator();
        if (transactions.hasNext())
        {
            return transactions.next();
        }

        return null;
    }

    public BigInteger getWork() throws WolkenException {
        return LargestHash.divide(ChainMath.targetIntegerFromBits(getBits()).add(BigInteger.ONE));
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    public void removeLastTransaction() {
        Iterator<Transaction> transactions = this.transactions.iterator();
        if (transactions.hasNext())
        {
            transactions.next();

            if (!transactions.hasNext()) {
                transactions.remove();
            }
        }
    }

    @Override
    public Iterator<Transaction> iterator() {
        return transactions.iterator();
    }
}
