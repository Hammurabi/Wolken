package org.wolkenproject.core;

public class MinedBlockCandidate extends CandidateBlock {
    private BlockIndex block;

    public MinedBlockCandidate(BlockIndex block) {
        super(chainWork);
        this.block = block;
    }

    @Override
    public BlockHeader getBlockHeader() {
        return block.getBlock().getBlockHeader();
    }

    @Override
    public BlockIndex getBlock() {
        return block;
    }

    @Override
    public boolean isFullBlockAvailable() {
        return true;
    }
}
