package org.wolkenproject.core;

public abstract class AbstractBlockChain implements Runnable {
    protected static final int                MaximumOrphanBlockQueueSize = 250_000_000;
    protected static final int                MaximumStaleBlockQueueSize  = 500_000_000;
    protected static final int                MaximumPoolBlockQueueSize   = 1_250_000_000;
}
