package org.wolkenproject.core;

import java.math.BigInteger;

/*
    Candidate blocks are sent by peers
    or are locally created when a miner
    finds a new block, only the PoW is
    verified before sending it to the
    BlockChain as a suggestion. So a
    secondary verification is required.
 */
public abstract class CandidateBlock implements Comparable<CandidateBlock> {
    private final BigInteger    chainWork;
    private final long          sequenceId;

    protected CandidateBlock(BigInteger chainWork) {
        this.chainWork  = chainWork;
        this.sequenceId = System.currentTimeMillis();
    }

    public abstract BlockHeader getBlockHeader();
    public abstract BlockIndex getBlock();
    public abstract boolean isFullBlockAvailable();

    public BigInteger getTotalChainWork() {
        return chainWork;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    @Override
    public int compareTo(CandidateBlock candidateBlock) {
        int compare = getTotalChainWork().compareTo(other.getTotalChainWork());

        if (compare > 0) {
            return -1;
        }

        if (compare < 0) {
            return 1;
        }

        if (getSequenceId() < other.getSequenceId()) {
            return -1;
        }

        if (getSequenceId() > other.getSequenceId()) {
            return 1;
        }

        if (getBlock().getTransactionCount() > other.getBlock().getTransactionCount()) {
            return 1;
        }

        if (getBlock().getTransactionCount() < other.getBlock().getTransactionCount()) {
            return -1;
        }

        return -1;
    }
}
