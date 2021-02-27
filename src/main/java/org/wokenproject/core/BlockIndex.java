package org.wokenproject.core;

import java.math.BigInteger;

public class BlockIndex {
    private Block       block;
    private BigInteger  chainWork;
    private int         height;

    public BlockIndex(Block block, BigInteger chainWork, int height)
    {
        this.block      = block;
        this.chainWork  = chainWork;
        this.height     = height;
    }

    public Block getBlock()
    {
        return block;
    }

    public BigInteger getChainWork()
    {
        return chainWork;
    }

    public int getHeight()
    {
        return height;
    }

    public Block generateNextBlock()
    {
        return new BlockIndex();
    }
}
