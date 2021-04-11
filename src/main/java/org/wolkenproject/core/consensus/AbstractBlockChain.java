package org.wolkenproject.core.consensus;

import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBlockChain implements Runnable {
    private final Context context;
    private final ReentrantLock mutex;

    public AbstractBlockChain(Context context) {
        this.context    = context;
        this.mutex      = new ReentrantLock();
    }

    public static AbstractBlockChain create(boolean pruned) {
        return null;
    }

    // attempt to make the 'candidate' into the best block, returns true if the operation is successful.
    protected abstract boolean makeBest(BlockIndex candidate);
    // broadcast the chain information to all peers.
    protected abstract void broadcastChain();
    // returns the best block of this chain.
    public abstract BlockIndex getBestBlock();
    // returns true if the block is better than our current best block.
    protected abstract boolean isBetterBlock(BlockIndex candidate);
    // get the next block from the block pool.
    protected abstract BlockIndex getCandidate();
    // must be called before starting the main loop.
    protected abstract void initialize();
    // load the last saved block, or re-generate a genesis block if this is a fresh chain.
    protected abstract void loadBestBlock();
    // save a recovery checkpoint.
    protected abstract void setBestBlock(BlockIndex block);
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
    public abstract void suggest(Set<CandidateBlock> blocks);
    // suggest this block and add it to the pool.
    public abstract void suggest(CandidateBlock block);
    // return the current chain height.
    public abstract int getHeight();
    // makes the block and all it's children 'stale'.
    public abstract void staleBlock(byte[] hash);
    // generates a new block where height is 'bestblock.height + 1'
    public abstract BlockIndex fork();
}
