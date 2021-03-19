package org.wolkenproject.core;

import org.wolkenproject.utils.PriorityHashQueue;

import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBlockChain implements Runnable {
    protected static final int                MaximumOrphanBlockQueueSize = 250_000_000;
    protected static final int                MaximumStaleBlockQueueSize  = 500_000_000;
    protected static final int                MaximumPoolBlockQueueSize   = 1_250_000_000;

    // the current higest block in the chain
    private BlockIndex                      tip;
    // contains blocks that have no parents.
    private PriorityHashQueue<BlockIndex> orphanedBlocks;
    // contains blocks that were valid pre-fork.
    private PriorityHashQueue<BlockIndex>   staleBlocks;
    // contains blocks sent from peers.
    private PriorityHashQueue<BlockIndex>   blockPool;
    // a mutex
    private ReentrantLock lock;

    public AbstractBlockChain() {
        lock            = new ReentrantLock();
        orphanedBlocks  = new PriorityHashQueue<>(BlockIndex.class);
        staleBlocks     = new PriorityHashQueue<>(BlockIndex.class);
        blockPool       = new PriorityHashQueue<>(BlockIndex.class);
    }

    @Override
    public void run() {
    }

    public void suggest(BlockIndex index) {
    }
}
