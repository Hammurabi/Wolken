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
public abstract class CandidateBlockChain implements Comparable<CandidateBlockChain> {
    private final long          sequenceId;
    private final int           transactionCount;

    protected CandidateBlockChain(int transactionCount) {
        this.sequenceId = System.currentTimeMillis();
        this.transactionCount = transactionCount;
    }

    // returns the 'best block' header of the chain.
    public abstract BlockHeader getBlockHeader();
    // returns the 'best block' of the chain.
    public abstract BlockIndex getBlock();
    // returns true if the 'getBlock' will return a value.
    public abstract boolean isFullBlockAvailable();
    // returns true if the 'getChain' will return a value.
    public abstract boolean isFullChainAvailable();

    public BigInteger getTotalChainWork() {
        return chainWork;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    @Override
    public int compareTo(CandidateBlockChain candidateBlock) {
        int compare = getTotalChainWork().compareTo(candidateBlock.getTotalChainWork());

        if (compare > 0) {
            return -1;
        }

        if (compare < 0) {
            return 1;
        }

        if (getSequenceId() < candidateBlock.getSequenceId()) {
            return -1;
        }

        if (getSequenceId() > candidateBlock.getSequenceId()) {
            return 1;
        }

        if (getTransactionCount() > candidateBlock.getTransactionCount()) {
            return 1;
        }

        if (getTransactionCount() < candidateBlock.getTransactionCount()) {
            return -1;
        }

        return -1;
    }
}
