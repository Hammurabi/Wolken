package org.wolkenproject.core.consensus;

import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.messages.Inv;
import org.wolkenproject.utils.HashQueue;

import java.util.Arrays;
import java.util.Set;

public class BasicChain extends AbstractBlockChain {
    protected static final int                MaximumOrphanBlockQueueSize   = 250_000_000;
    protected static final int                MaximumStaleBlockQueueSize    = 500_000_000;
    protected static final int                MaximumPoolBlockQueueSize     = 1_250_000_000;

    // the current higest block in the chain
    private BlockIndex                  bestBlock;
    // contains blocks sent from peers.
    private HashQueue<CandidateBlock>   candidateQueue;
    // keep track of broadcasted blocks.
    private byte                        lastBroadcast[];

    public BasicChain(Context context) {
        super(context);
        this.lastBroadcast = new byte[32];
    }

    @Override
    protected final void setBlock(int height, BlockIndex block) {
        if (containsBlock(height)) {
            removeBlock(getBlockHash(height));
        }

        getContext().getDatabase().storeBlock(height, block);
    }

    @Override
    protected boolean containsBlock(int height) {
        return false;
    }

    @Override
    public boolean containsBlock(byte[] hash) {
        return false;
    }

    @Override
    protected byte[] getBlockHash(int height) {
        return getContext().getDatabase().findBlockHash(height);
    }

    @Override
    protected void removeBlock(byte[] hash) {
        getContext().getDatabase().deleteBlock(hash);
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
    protected void addOrphan(BlockIndex block) {

    }

    @Override
    protected void initialize() {
        // load the last checkpoint.
        loadBestBlock();
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
    protected boolean isBetterBlock(CandidateBlock candidate) {
        return candidate.getTotalChainWork().compareTo(getBestBlock().getTotalChainWork()) > 0;
    }

    @Override
    protected CandidateBlock getCandidate() {
        getMutex().lock();
        try {
            if (candidateQueue.hasElements()) {
                return candidateQueue.poll();
            }

            return null;
        } finally {
            getMutex().unlock();
        }
    }

    @Override
    public int getHeight() {
        BlockIndex best = getBestBlock();
        if (best == null) {
            return 0;
        }

        return best.getHeight();
    }

    @Override
    public ChainFork getFork(byte[] hash) {
        return null;
    }

    @Override
    public BlockIndex fork() {
        return null;
    }

    @Override
    public void makeStale(byte[] hash) {
        removeBlock(hash);
    }

    @Override
    public BlockIndex getBlock(byte[] hash) {
        return getContext().getDatabase().findBlock(hash);
    }

    @Override
    public void suggest(Set<CandidateBlock> blocks) {
        for (CandidateBlock block : blocks) {
            suggest(block);
        }
    }

    @Override
    public void suggest(CandidateBlock block) {
        getMutex().lock();
        try {
            candidateQueue.add(block, block.getHash());
        } finally {
            getMutex().unlock();
        }
    }

    @Override
    protected void makeBest(CandidateBlock candidate) {
        // merge the block candidate.
        candidate.merge(this);
        // destroy this block candidate.
        candidate.destroy();
    }

    @Override
    protected void broadcastChain() {
        getMutex().lock();
        try {
            // retrieve the best block.
            BlockIndex bestBlock = getBestBlock();

            // nullpointer checks.
            if (bestBlock == null) {
                return;
            }

            // check that we did not broadcast this block before.
            if (Arrays.equals(lastBroadcast, bestBlock.getHash())) {
                return;
            }

            // make an inventory message.
            Message inv = new Inv(getContext().getNetworkParameters().getVersion(), Inv.Type.Block, bestBlock.getHash());

            // broadcast the inventory message.
            getContext().getServer().broadcast(inv);

            // set the last broadcast block to the newly broadcast block hash.
            lastBroadcast = Arrays.copyOf(bestBlock.getHash(), lastBroadcast.length);
        } finally {
            getMutex().unlock();
        }
    }

    @Override
    public BlockIndex getBestBlock() {
        getMutex().lock();
        try {
            return bestBlock;
        } finally {
            getMutex().unlock();
        }
    }

    @Override
    public void run() {
        // initialize the chain.
        initialize();

        // to keep track of time.
        long lastCheck = System.currentTimeMillis();

        // enter the main loop.
        while ( getContext().isRunning() ) {
            // get a candidate block from the block pool.
            CandidateBlock candidate = getCandidate();

            // candidate could be null.
            if (candidate == null) {
                continue;
            }

            // check if the candidate is better than our block.
            if (isBetterBlock(candidate)) {
                // make the candidate our best block.
                makeBest(candidate);
            }

            if (System.currentTimeMillis() - lastCheck > 5000L) {
                broadcastChain();
                lastCheck = System.currentTimeMillis();
            }
        }
    }
}
