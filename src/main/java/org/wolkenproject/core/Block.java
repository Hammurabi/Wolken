package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.ChainMath;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Block extends BlockHeader {
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

    public final int countLength() {
        long transactionLength = 0;
        for (Transaction transaction : transactions) {
            transactionLength += transaction.calculateSize();
        }

        return BlockHeader.Size + VarInt.SizeOfCompactUin32(transactions.size(), false) + transactionLength;
    }

    /*
        returns a new block header
     */
    public final BlockHeader getBlockHeader() {
        return new BlockHeader(getVersion(), getTimestamp(), getParentHash(), getMerkleRoot(), getBits(), getNonce());
    }

    protected byte[] calculateMerkleRoot() {
        Queue<byte[]> txids = new LinkedBlockingQueue<>();
        for (Transaction transaction : transactions) {
            txids.add(transaction.getTransactionID());
        }

        while (txids.size() > 1) {
            txids.add(HashUtil.sha256d(Utils.concatenate(txids.poll(), txids.poll())));
        }

        return txids.poll();
    }

    public void build() {
        setMerkleRoot(calculateMerkleRoot());
    }

    public boolean verify() {
        if (!Utils.equals(calculateMerkleRoot(), getMerkleRoot())) return false;

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
}
