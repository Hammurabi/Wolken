package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;

import java.math.BigInteger;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain implements Runnable {
    private BlockIndex      tip;
    private byte[]          chainWork;
    private Queue<Block>    orphanedBlocks;

    private ReentrantLock   lock;

    public final void consensus()
    {
    }

    @Override
    public void run() {
    }

    public BlockIndex makeGenesisBlock() throws WolkenException {
        Block genesis = new Block(0, new byte[Block.UniqueIdentifierLength], 0);
        return new BlockIndex(genesis, BigInteger.ZERO, 0);
    }

    public BlockIndex makeBlock() throws WolkenException {
        lock.lock();
        try {
            return tip.generateNextBlock();
        }
        finally {
            lock.unlock();
        }
    }
}
