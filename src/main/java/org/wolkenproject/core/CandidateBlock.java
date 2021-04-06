package org.wolkenproject.core;

/*
    Candidate blocks are sent by peers
    or are locally created when a miner
    finds a new block, only the PoW is
    verified before sending it to the
    BlockChain as a suggestion. So a
    secondary verification is required.
 */
public abstract class CandidateBlock {
    public abstract BlockHeader getBlockHeader();
    public abstract BlockIndex getBlock();
}
