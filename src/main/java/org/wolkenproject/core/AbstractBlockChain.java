package org.wolkenproject.core;

import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBlockChain implements Runnable {
    protected static final int                MaximumOrphanBlockQueueSize = 250_000_000;
    protected static final int                MaximumStaleBlockQueueSize  = 500_000_000;
    protected static final int                MaximumPoolBlockQueueSize   = 1_250_000_000;

    private ReentrantLock lock;

    public AbstractBlockChain() {
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
    }
}
