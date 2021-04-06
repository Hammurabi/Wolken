package org.wolkenproject.core;

import org.wolkenproject.utils.HashQueue;

public abstract class BasicChain extends AbstractBlockChain {
    protected static final int                MaximumOrphanBlockQueueSize   = 250_000_000;
    protected static final int                MaximumStaleBlockQueueSize    = 500_000_000;
    protected static final int                MaximumPoolBlockQueueSize     = 1_250_000_000;

    // the current higest block in the chain
    private BlockIndex                  tip;
    // contains blocks that have no parents or ancestors.
    private HashQueue<BlockIndex>       orphanedBlocks;
    // contains blocks that were valid pre-fork.
    private HashQueue<BlockIndex>       staleBlocks;
    // contains blocks sent from peers.
    private HashQueue<CandidateBlockChain>   blockPool;

    public BasicChain(Context context) {
        super(context);
    }

    @Override
    protected final void setBlock(int height, BlockIndex block) {
        if (containsBlock(height)) {
            removeBlock(getBlockHash(height));
        }

        getContext().getDatabase().storeBlock(height, block);
    }

    @Override
    protected void markRejected(byte[] hash) {
        getContext().getDatabase().markRejected(hash);
    }

    @Override
    protected boolean isRejected(byte[] hash) {
        return getContext().getDatabase().isRejected(hash);
    }

    @Override
    protected void initialize() {
        // load the last checkpoint.
        loadBestBlock();
    }

    @Override
    protected boolean isBetterBlock(BlockIndex candidate) {
        return candidate.getTotalChainWork().compareTo(getBestBlock().getTotalChainWork()) > 0;
    }

    @Override
    public int getHeight() {
        BlockIndex best = getBestBlock();
        if (best == null) {
            return 0;
        }

        return best.getHeight();
    }

    @Override
    protected boolean makeBest(BlockIndex candidate) {
        return false;
    }

    @Override
    public void run() {
        // initialize the chain.
        initialize();

        // enter the main loop.
        while ( getContext().isRunning() ) {
            // get a candidate block from the block pool.
            BlockIndex candidate = getCandidate();
            // check if the candidate is better than our block.
            if (isBetterBlock(candidate)) {
                // fully validate the block and generate the state change.
                if (candidate.verify()) {
                    // make the candidate our best block.
                    if (makeBest(candidate)) {
                        // if the operation succeeds, then we broadcast the new block to our peers.
                        broadcastChain();
                    }
                }
            }
        }
    }
}
