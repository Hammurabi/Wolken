package org.wolkenproject.core;

import java.util.Set;

public class FullChain extends BasicChain {
    public FullChain(Context context) {
        super(context);
    }

    @Override
    protected void broadcastChain() {

    }

    @Override
    protected BlockIndex getBestBlock() {
        return null;
    }

    @Override
    protected BlockIndex getCandidate() {
        return null;
    }

    @Override
    protected void loadBestBlock() {

    }

    @Override
    protected void setBestBlock(BlockIndex block) {

    }

    @Override
    public boolean verifyBlock(BlockIndex block) {
        return false;
    }

    @Override
    protected boolean containsBlock(int height) {
        return false;
    }

    @Override
    protected boolean containsBlock(byte[] hash) {
        return false;
    }

    @Override
    protected byte[] getBlockHash(int height) {
        return new byte[0];
    }

    @Override
    protected void removeBlock(byte[] hash) {

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
}
