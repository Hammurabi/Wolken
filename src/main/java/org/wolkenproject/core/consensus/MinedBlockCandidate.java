package org.wolkenproject.core.consensus;

import org.wolkenproject.core.AbstractBlockChain;
import org.wolkenproject.core.BlockHeader;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.consensus.CandidateBlock;

import java.util.List;

public class MinedBlockCandidate extends CandidateBlock {
    private BlockIndex block;

    public MinedBlockCandidate(Context context, BlockIndex block) {
        super(context);
        this.block = block;
    }

    public BlockHeader getBlockHeader() {
        return block.getBlock().getBlockHeader();
    }

    public BlockIndex getBlock() {
        return block;
    }

    public boolean isFullBlockAvailable() {
        return true;
    }

    @Override
    public boolean verify() {
        return false;
    }

    @Override
    public void merge(AbstractBlockChain chain) {

    }

    @Override
    public List<BlockHeader> getChain() {
        return null;
    }

    @Override
    public boolean isChainAvailable() {
        return false;
    }

    @Override
    public boolean destroy() {
        return false;
    }
}
