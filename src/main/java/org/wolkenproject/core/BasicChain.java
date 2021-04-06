package org.wolkenproject.core;

public abstract class BasicChain extends AbstractBlockChain {
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
        return getBestBlock().getHeight();
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
