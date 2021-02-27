package org.wolkenproject.core;

import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain implements Runnable {
    private Block           tip;
    private byte[]          chainWork;
    private Queue<Block>    orphanedBlocks;

    private ReentrantLock   lock;

    public final void consensus()
    {
    }

    @Override
    public void run() {
    }
}
