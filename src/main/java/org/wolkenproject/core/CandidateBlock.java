package org.wolkenproject.core;

import java.math.BigInteger;
import java.util.List;

/*
    Candidate blocks are sent by peers
    or are locally created when a miner
    finds a new block, only the PoW is
    verified before sending it to the
    BlockChain as a suggestion. So a
    secondary verification is required.
 */
public abstract class CandidateBlock implements Comparable<CandidateBlock> {
    private final long          sequenceId;
    private final int           transactionCount;
    private final Context       context;
    private BigInteger          chainWork;

    protected CandidateBlock(Context context, int transactionCount) {
        this.sequenceId = System.currentTimeMillis();
        this.transactionCount = transactionCount;
        this.context = context;
        this.chainWork = null;
    }

    public Context getContext() {
        return context;
    }

    // returns the 'best block' header of the chain.
    public abstract BlockHeader getBlockHeader();
    // returns the 'best block' of the chain.
    public abstract BlockIndex getBlock();
    // returns true if the 'getBlock' will return a value.
    public abstract boolean isFullBlockAvailable();
    // returns a list of all blocks from and excluding the most recent common ancestor to and including the best block.
    public abstract List<BlockHeader> getChain();
    // returns true if the 'getChain' will return a value.
    public abstract boolean isChainAvailable();
    // returns true if the full blocks have been downloaded entirely.
    public abstract boolean areBlocksAvailable();
    // deletes all data in storage, must be called before omitting thins candidate from the block pool.
    public abstract boolean destroy();

    public BigInteger getTotalChainWork() {
        if (chainWork == null && isChainAvailable()) {
            List<BlockHeader> chain = getChain();
            byte mostRecentCommonAncestor[] = chain.get(0).getParentHash();
            BlockMetadata metadata = getContext().getDatabase().findBlockMetaData(mostRecentCommonAncestor);
            BigInteger startWork = BigInteger.ZERO;

            if (metadata != null) {
                startWork = metadata.getChainWork();

                for (BlockHeader header : chain) {
                    startWork = startWork.add(header.getWork());
                }

                return startWork;
            }
        } else {
            return chainWork;
        }

        return BigInteger.ZERO;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    @Override
    public int compareTo(CandidateBlock candidateBlock) {
        int compare = getTotalChainWork().compareTo(candidateBlock.getTotalChainWork());

        if (compare > 0) {
            return -1;
        }

        if (compare < 0) {
            return 1;
        }

        // if this candidate came first then it should be considered 'better'.
        if (getSequenceId() < candidateBlock.getSequenceId()) {
            return -1;
        }

        // if this candidate came later then it should be considered 'worse'.
        if (getSequenceId() > candidateBlock.getSequenceId()) {
            return 1;
        }

        // if this candidate chain has more transactions then it should be considered 'better'.
        if (getTransactionCount() > candidateBlock.getTransactionCount()) {
            return -1;
        }

        // if this candidate chain has more transactions then it should be considered 'better'.
        if (getTransactionCount() < candidateBlock.getTransactionCount()) {
            return 1;
        }

        return -1;
    }
}
