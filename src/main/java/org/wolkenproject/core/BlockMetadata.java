package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class BlockMetadata extends SerializableI {
    private BlockHeader blockHeader;
    private int height;
    private int transactionCount;
    private int eventCount;
    private long totalValue;
    private long fees;
    private BigInteger chainWork;

    public BlockMetadata() {
        this(new BlockHeader(), 0, 0, 0, 0, 0, BigInteger.ZERO);
    }

    public BlockMetadata(BlockHeader blockHeader, int height, int transactionCount, int eventCount, long totalValue, long fees, BigInteger chainWork) {
        this.blockHeader = blockHeader;
        this.height = height;
        this.transactionCount = transactionCount;
        this.eventCount = eventCount;
        this.totalValue = totalValue;
        this.fees = fees;
        this.chainWork = chainWork;
    }

    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public int getHeight() {
        return height;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public int getEventCount() {
        return eventCount;
    }

    public long getTotalValue() {
        return totalValue;
    }

    public long getFees() {
        return fees;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        getBlockHeader().write(stream);
        VarInt.writeCompactUInt32(getHeight(), false, stream);
        VarInt.writeCompactUInt32(getTransactionCount(), false, stream);
        VarInt.writeCompactUInt32(getEventCount(), false, stream);
        VarInt.writeCompactUInt64(getTotalValue(), false, stream);
        VarInt.writeCompactUInt64(getFees(), false, stream);
        VarInt.writeCompactUint256(getChainWork(), true, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        getBlockHeader().read(stream);
        height = VarInt.readCompactUInt32(false, stream);
        transactionCount = VarInt.readCompactUInt32(false, stream);
        eventCount = VarInt.readCompactUInt32(false, stream);
        totalValue = VarInt.readCompactUInt64(false, stream);
        fees = VarInt.readCompactUInt64(false, stream);
        chainWork = VarInt.readCompactUint256(true, stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new BlockMetadata();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(BlockMetadata.class);
    }
}
