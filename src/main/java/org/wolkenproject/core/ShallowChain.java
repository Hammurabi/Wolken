package org.wolkenproject.core;

import java.util.Set;

public class ShallowChain extends AbstractBlockChain {
    public ShallowChain(Context context) {
        super(context);
    }

    @Override
    public boolean verifyBlock(BlockIndex block) {
        return false;
    }

    @Override
    protected void setBlock(int height, BlockIndex block) {

    }

    @Override
    protected void markRejected(byte[] block) {

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
