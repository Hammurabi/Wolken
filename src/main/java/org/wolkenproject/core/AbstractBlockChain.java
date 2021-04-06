package org.wolkenproject.core;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBlockChain implements Runnable {
    private final Context       context;
    private final ReentrantLock mutex;

    public AbstractBlockChain(Context context) {
        this.context    = context;
        this.mutex      = new ReentrantLock();
    }

    // returns true if the block is valid (full validation).
    public abstract boolean verifyBlock(BlockIndex block);
    // return the context.
    public Context getContext() {
        return context;
    }
    // return the mutex.
    public ReentrantLock getMutex() {
        return mutex;
    }
    // set the block at 'height' to 'block', if a previous block exists then it should be replaced.
    protected abstract void setBlock(int height, BlockIndex block);
    // return true if the chain height is larger than or equal to 'height'.
    protected abstract boolean containsBlock(int height);
    // return true if block with hash 'hash' exists.
    protected abstract boolean containsBlock(byte hash[]);
    // return the hash of block at height 'height'.
    protected abstract byte[] getBlockHash(int height);
    // remove the block with hash 'hash'.
    protected abstract void removeBlock(byte hash[]);
    // mark block as rejected, this block or it's children will never be considered valid.
    protected abstract void markRejected(byte hash[]);
    // returns true if this block has been rejected.
    protected abstract boolean isRejected(byte[] hash);
    // makes the block an orphan, meaning it does not have any ancestors.
    protected abstract void addOrphan(BlockIndex block);
    // stales block, it's previously valid, but not the best.
    protected abstract void addStale(BlockIndex block);
    // suggest these blocks and add them to the pool.
    public abstract void suggest(Set<BlockIndex> blocks);
    // suggest this block and add it to the pool.
    public abstract void suggest(BlockIndex block);
    // return the current chain height.
    public abstract int getHeight();
}
