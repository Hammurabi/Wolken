package org.wolkenproject.core;

import java.util.Set;

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
    protected void markRejected(byte[] block) {
        getContext().getDatabase().markRejected(block);
    }

    @Override
    protected boolean isRejected(byte[] hash) {
        return false;
    }

    @Override
    protected void addOrphan(BlockIndex block) {

    }

    @Override
    protected void addStale(BlockIndex block) {

    }

    @Override
    public void suggest(Set<BlockIndex> blocks) {

    }

    @Override
    public void suggest(BlockIndex block) {

    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void run() {

    }
}
