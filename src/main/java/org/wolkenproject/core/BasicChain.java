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
    }

    @Override
    public void run() {
        // initialize the chain.
        initialize();

        // enter the main loop.
        while ( getContext().isRunning() ) {
        }
    }
}
