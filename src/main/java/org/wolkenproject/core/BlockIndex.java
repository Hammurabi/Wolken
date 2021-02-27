package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class BlockIndex extends SerializableI {
    private Block block;
    private BigInteger chainWork;
    private int height;

    public BlockIndex() {
        this(new Block(), BigInteger.ZERO, 0);
    }

    public BlockIndex(Block block, BigInteger chainWork, int height) {
        this.block = block;
        this.chainWork = chainWork;
        this.height = height;
    }

    public Block getBlock() {
        return block;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }

    public int getHeight() {
        return height;
    }

    public BlockIndex generateNextBlock() throws WolkenException {
        return new BlockIndex(new Block(), chainWork.add(block.getWork()), height + 1);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        block.write(stream);
        byte chainWork[] = this.chainWork.toByteArray();
        stream.write(chainWork.length);
        stream.write(chainWork);
        Utils.writeInt(height, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        block.read(stream);
        byte length = (byte) stream.read();
        byte chainWork[] = new byte[length];
        stream.read(chainWork);
        this.chainWork = new BigInteger(chainWork);
        stream.read(chainWork, 0, 4);
        height = Utils.makeInt(chainWork);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new BlockIndex();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(BlockIndex.class);
    }
}
