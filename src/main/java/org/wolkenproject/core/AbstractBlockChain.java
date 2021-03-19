package org.wolkenproject.core;

import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.messages.BlockList;
import org.wolkenproject.network.messages.RequestBlocks;
import org.wolkenproject.network.messages.RequestHeadersBefore;
import org.wolkenproject.utils.Logger;
import org.wolkenproject.utils.PriorityHashQueue;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBlockChain implements Runnable {
    protected static final int                MaximumOrphanBlockQueueSize = 250_000_000;
    protected static final int                MaximumStaleBlockQueueSize  = 500_000_000;
    protected static final int                MaximumPoolBlockQueueSize   = 1_250_000_000;

    // the current higest block in the chain
    private BlockIndex                      tip;
    // contains blocks that have no parents.
    private PriorityHashQueue<BlockIndex>   orphanedBlocks;
    // contains blocks that were valid pre-fork.
    private PriorityHashQueue<BlockIndex>   staleBlocks;
    // contains blocks sent from peers.
    private PriorityHashQueue<BlockIndex>   blockPool;
    // a reference to context
    private Context                         context;
    // a mutex
    private ReentrantLock                   lock;

    public AbstractBlockChain() {
        lock            = new ReentrantLock();
        orphanedBlocks  = new PriorityHashQueue<>(BlockIndex.class);
        staleBlocks     = new PriorityHashQueue<>(BlockIndex.class);
        blockPool       = new PriorityHashQueue<>(BlockIndex.class);
    }

    @Override
    public void run() {
    }

    private boolean isCommonAncestor(BlockHeader blockHeader) {
        return Context.getInstance().getDatabase().checkBlockExists(blockHeader.getHashCode());
    }

    public BlockHeader findCommonAncestor(BlockIndex block) {
        // request block headers
        Message response = Context.getInstance().getServer().broadcastRequest(new RequestHeadersBefore(Context.getInstance().getNetworkParameters().getVersion(), block.getHash(), 1024, block.getBlock()));
        BlockHeader commonAncestor = null;

        if (response != null) {
            Collection<BlockHeader> headers = response.getPayload();

            while (headers != null) {
                Iterator<BlockHeader> iterator = headers.iterator();

                BlockHeader header = iterator.next();
                if (isCommonAncestor(header)) {
                    commonAncestor = header;
                }

                // loop headers to find a common ancestor
                while (iterator.hasNext()) {
                    header = iterator.next();

                    if (isCommonAncestor(header)) {
                        commonAncestor = header;
                    }
                }

                // find older ancestor
                if (commonAncestor == null) {
                    response = Context.getInstance().getServer().broadcastRequest(new RequestHeadersBefore(Context.getInstance().getNetworkParameters().getVersion(), header.getHashCode(), 4096, header));

                    if (response != null) {
                        headers = response.getPayload();
                    }
                }
            }
        }

        if (commonAncestor != null) {
            Logger.alert("found common ancestor" + block + " for block" + block);
        }

        return commonAncestor;
    }

    private void replaceTip(BlockIndex block) throws WolkenException {
        BlockHeader commonAncestor = findCommonAncestor(block);

        if (commonAncestor != null) {
            // stale the current tip
            addStale(getTip());

            byte previousHash[] = getTip().getBlock().getParentHash();
            if (Utils.equals(block.getBlock().getParentHash(), previousHash)) {
                setTip(block);
                return;
            }

            rollbackIntoExistingParent(block.getBlock().getParentHash(), block.getHeight() - 1);
        } else {
            addOrphan(block);
        }
    }

    private void rollback(BlockIndex block) throws WolkenException {
        BlockHeader commonAncestor = findCommonAncestor(block);

        if (commonAncestor != null) {
            BlockIndex currentBlock = tip;

            while (currentBlock.getHeight() != block.getHeight()) {
                deleteBlockIndex(currentBlock, true);
                currentBlock = currentBlock.previousBlock();
            }

            setTip(currentBlock.previousBlock());
            replaceTip(block);
        } else {
            addOrphan(block);
        }
    }

    private void setNextGapped(BlockIndex block) throws WolkenException {
        BlockHeader commonAncestor = findCommonAncestor(block);

        if (commonAncestor != null) {
            setTip(block);
            rollbackIntoExistingParent(block.getBlock().getParentHash(), block.getHeight() - 1);
        } else {
            addOrphan(block);
        }
    }

    private boolean hasOrphans() {
        lock.lock();
        try {
            return !orphanedBlocks.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    private BlockIndex nextOrphan() {
        lock.lock();
        try {
            return orphanedBlocks.poll();
        } finally {
            lock.unlock();
        }
    }

    private boolean hasBlocksInPool() {
        lock.lock();
        try {
            return !blockPool.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    private BlockIndex nextFromPool() {
        lock.lock();
        try {
            return blockPool.poll();
        } finally {
            lock.unlock();
        }
    }

    private void setNext(BlockIndex block) throws WolkenException {
        byte previousHash[] = block.getBlock().getParentHash();

        if (Utils.equals(previousHash, tip.getHash())) {
            setTip(block);
            return;
        }

        BlockHeader commonAncestor = findCommonAncestor(block);

        if (commonAncestor != null) {
            rollbackIntoExistingParent(block.getBlock().getParentHash(), block.getHeight() - 1);
        } else {
            addOrphan(block);
        }
    }

    private boolean rollbackIntoExistingParent(byte[] parentHash, int height) throws WolkenException {
        // check that the block exists
        if (Context.getInstance().getDatabase().checkBlockExists(parentHash)) {
            return true;
        }

        // we must request it in case it doesn't
        BlockIndex parent = requestBlock(parentHash);
        while (parent != null) {
            replaceBlockIndex(height, parent);
            height      --;
            parentHash  = parent.getBlock().getParentHash();

            if (Context.getInstance().getDatabase().checkBlockExists(parentHash) || height == -1) {
                updateIndices(parent);
                return true;
            }

            parent = requestBlock(parentHash);
        }

        return false;
    }

    private void updateIndices(BlockIndex index) throws WolkenException {
        while (true) {
            index.recalculateChainWork();

            if (!index.hasNext()) {
                return;
            }

            index = index.next();
        }
    }

    private void setBlockIndex(int height, BlockIndex block) {
        Context.getInstance().getDatabase().setBlockIndex(height, block);
    }

    private void replaceBlockIndex(int height, BlockIndex block) {
        BlockIndex previousIndex = Context.getInstance().getDatabase().findBlock(height);
        if (previousIndex != null) {
            addStale(previousIndex);
        }

        Context.getInstance().getDatabase().setBlockIndex(height, block);
    }

    private void deleteBlockIndex(int height, boolean orphan) {
        BlockIndex block = Context.getInstance().getDatabase().findBlock(height);
        deleteBlockIndex(block, orphan);
    }

    private void deleteBlockIndex(BlockIndex block, boolean orphan) {
        if (orphan) {
            addStale(block);
        }

        Context.getInstance().getDatabase().deleteBlock(block.getHeight());
    }

    private BlockIndex requestBlock(byte hash[]) {
        Message request = new RequestBlocks(Context.getInstance().getNetworkParameters().getVersion(), hash);
        Message response= Context.getInstance().getServer().broadcastRequest(request);

        if (response != null && response instanceof BlockList) {
            Collection<BlockIndex> blocks = response.getPayload();
            if (blocks != null && !blocks.isEmpty()) {
                blocks.iterator().next();
            }
        }

        return null;
    }

    private void setTip(BlockIndex block) {
        tip = block;
        Context.getInstance().getDatabase().setTip(block);
        replaceBlockIndex(block.getHeight(), block);
    }

    private void addOrphan(BlockIndex block) {
        lock.lock();
        try {
            orphanedBlocks.add(block);

            // calculate the maximum blocks allowed in the queue.
            int maximumBlocks   = MaximumOrphanBlockQueueSize / Context.getInstance().getNetworkParameters().getMaxBlockSize();
            int Threshold       = (MaximumOrphanBlockQueueSize / 4) / Context.getInstance().getNetworkParameters().getMaxBlockSize();

            // remove any blocks that are too far back in the queue.
            if (orphanedBlocks.size() - maximumBlocks > Threshold) {
                trimOrphans(maximumBlocks);
            }
        } finally {
            lock.unlock();
        }
    }

    private void addStale(BlockIndex block) {
        lock.lock();
        try {
            staleBlocks.add(block);

            // calculate the maximum blocks allowed in the queue.
            int maximumBlocks   = MaximumStaleBlockQueueSize / Context.getInstance().getNetworkParameters().getMaxBlockSize();
            int Threshold       = (MaximumStaleBlockQueueSize / 4) / Context.getInstance().getNetworkParameters().getMaxBlockSize();

            // remove any blocks that are too far back in the queue.
            if (staleBlocks.size() - maximumBlocks > Threshold) {
                trimStales(maximumBlocks);
            }
        } finally {
            lock.unlock();
        }
    }

    public void pool(BlockIndex block) {
        lock.lock();
        try {
            blockPool.add(block);

            // calculate the maximum blocks allowed in the queue.
            int maximumBlocks   = MaximumPoolBlockQueueSize / Context.getInstance().getNetworkParameters().getMaxBlockSize();
            int Threshold       = (MaximumPoolBlockQueueSize / 4) / Context.getInstance().getNetworkParameters().getMaxBlockSize();

            // remove any blocks that are too far back in the queue.
            if (blockPool.size() - maximumBlocks > Threshold) {
                trimPool(maximumBlocks);
            }
        } finally {
            lock.unlock();
        }
    }

    private void trimOrphans(int newLength) {
        orphanedBlocks.removeTails(newLength);
    }

    private void trimStales(int newLength) {
        staleBlocks.removeTails(newLength);
    }

    private void trimPool(int newLength) {
        blockPool.removeTails(newLength);
    }

    public BlockIndex makeGenesisBlock() {
        Block genesis = new Block(new byte[Block.UniqueIdentifierLength], Context.getInstance().getNetworkParameters().getDefaultBits());
        genesis.addTransaction(Transaction.newMintTransaction("", Context.getInstance().getNetworkParameters().getMaxReward(), Context.getInstance().getNetworkParameters().getFoundingAddresses()));
        genesis.setNonce(0);
        return new BlockIndex(genesis, BigInteger.ZERO, 0);
    }

    public BlockIndex makeBlock() throws WolkenException {
        lock.lock();
        try {
            return tip.generateNextBlock();
        }
        finally {
            lock.unlock();
        }
    }

    public BlockIndex getTip() {
        lock.lock();
        try {
            return tip;
        }
        finally {
            lock.unlock();
        }
    }

    public boolean contains(byte[] hash) {
        lock.lock();
        try {
            return orphanedBlocks.containsKey(hash) || staleBlocks.containsKey(hash) || blockPool.containsKey(hash);
        }
        finally {
            lock.unlock();
        }
    }

    public Queue<BlockIndex> getOrphanedBlocks() {
        lock.lock();
        try {
            return new PriorityQueue<>(orphanedBlocks);
        }
        finally {
            lock.unlock();
        }
    }

    public BlockIndex getBlock(byte[] hash) {
        lock.lock();
        try {
            return orphanedBlocks.getByHash(hash);
        }
        finally {
            lock.unlock();
        }
    }

    public Set<byte[]> getInv() {
        lock.lock();
        try {
            Set<byte[]> hashes = new LinkedHashSet<>();
            BlockIndex index = tip;
            for (int i = 0; i < 16_384; i ++) {
                hashes.add(index.getHash());
                index = index.previousBlock();
                if (index == null) {
                    break;
                }
            }

            return hashes;
        }
        finally {
            lock.unlock();
        }
    }

    public void suggest(Set<BlockIndex> blocks) {
        for (BlockIndex block : blocks) {
            pool(block);
        }
    }

    public void suggest(BlockIndex block) {
        pool(block);
    }

    public int getHeight() {
        BlockIndex tip = getTip();
        if (tip != null) {
            return tip.getHeight();
        }

        return 0;
    }
}
