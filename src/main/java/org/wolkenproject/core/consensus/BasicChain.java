package org.wolkenproject.core.consensus;

import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.consensus.AbstractBlockChain;
import org.wolkenproject.core.consensus.CandidateBlock;
import org.wolkenproject.utils.HashQueue;

import java.util.Set;

public abstract class BasicChain extends AbstractBlockChain {
    protected static final int                MaximumOrphanBlockQueueSize   = 250_000_000;
    protected static final int                MaximumStaleBlockQueueSize    = 500_000_000;
    protected static final int                MaximumPoolBlockQueueSize     = 1_250_000_000;

    // the current higest block in the chain
    private BlockIndex                  bestBlock;
    // contains blocks that have no parents or ancestors.
    private HashQueue<BlockIndex>       orphanedBlocks;
    // contains blocks that were valid pre-fork.
    private HashQueue<BlockIndex>       staleBlocks;
    // contains blocks sent from peers.
    private HashQueue<CandidateBlock>   blockPool;

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
    public void suggest(Set<CandidateBlock> blocks) {
        for (CandidateBlock block : blocks) {
            suggest(block);
        }
    }

    @Override
    public void suggest(CandidateBlock block) {
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
            CandidateBlock candidate = getCandidate();
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
