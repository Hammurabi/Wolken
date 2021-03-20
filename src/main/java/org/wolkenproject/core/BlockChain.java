package org.wolkenproject.core;

import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.messages.*;
import org.wolkenproject.utils.*;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain implements Runnable {
    protected static final int                MaximumOrphanBlockQueueSize   = 250_000_000;
    protected static final int                MaximumStaleBlockQueueSize    = 500_000_000;
    protected static final int                MaximumPoolBlockQueueSize     = 1_250_000_000;

    // the current higest block in the chain
    private BlockIndex              tip;
    // contains blocks that have no parents or ancestors.
    private HashQueue<BlockIndex>   orphanedBlocks;
    // contains blocks that were valid pre-fork.
    private HashQueue<BlockIndex>   staleBlocks;
    // contains blocks sent from peers.
    private HashQueue<BlockIndex>   blockPool;
    // contains rejected blocks.
    private Context                 context;
    // a mutex
    private ReentrantLock mutex;

    public BlockChain(Context context) {
        this.context    = context;
        orphanedBlocks  = new PriorityHashQueue<>(BlockIndex.class);
        staleBlocks     = new PriorityHashQueue<>(BlockIndex.class);
        blockPool       = new PriorityHashQueue<>(BlockIndex.class);
        mutex           = new ReentrantLock();
        tip             = context.getDatabase().findTip();
    }

    @Override
    public void run() {
        long lastBroadcast  = System.currentTimeMillis();
        byte lastHash[]     = null;

        Logger.alert("attempting to reload chain from last checkpoint.");
        mutex.lock();
        try {
            tip = context.getDatabase().findTip();
            if (tip != null) {
                Logger.alert("loaded checkpoint successfully" + tip);
            } else {
                tip = makeGenesisBlock();
                Logger.alert("loaded genesis as checkpoint successfully" + tip);
            }
        } finally {
            mutex.unlock();
        }

        while (context.isRunning()) {
            if (System.currentTimeMillis() - lastBroadcast > (5 * 60_000L)) {
                int blocksToSend = 16384;
                lastBroadcast = System.currentTimeMillis();
            }

            if (hasBlocksInPool()) {
                // pull from suggested block pool
                BlockIndex block = nextFromPool();
                if (!block.verify()) {
                    markRejected(block.getHash());
                    continue;
                }

                try {
                    BlockIndex currentTip = getTip();

                    if (block.getChainWork().compareTo(currentTip.getChainWork()) > 0) {
                        // switch to this chain
                        if (block.getHeight() == currentTip.getHeight()) {
                            // if both blocks share the same height, then orphan the current tip.
                            replaceTip(block);
                        } else if (block.getHeight() == (currentTip.getHeight() + 1)) {
                            // if block is next in line then set as next block.
                            setNext(block);
                        } else if (block.getHeight() > currentTip.getHeight()) {
                            // if block next but with some blocks missing then we fill the gap.
                            setNextGapped(block);
                        } else if (block.getHeight() < currentTip.getHeight()) {
                            // if block is earlier then we must roll back the chain.
                            rollback(block);
                        }
                    }
                } catch (WolkenException e) {
                    e.printStackTrace();
                }
            }

            byte tipHash[] = getTip().getHash();

            // everytime the tip hash changes, broadcast it to connected nodes.
            if (lastHash != null && !Utils.equals(tipHash, lastHash)) {
                Set<byte[]> hashCodes = new LinkedHashSet<>();
                hashCodes.add(tipHash);
                lastHash = tipHash;

                try {
                    context.getServer().broadcast(new Inv(context.getNetworkParameters().getVersion(), Inv.Type.Block, hashCodes));
                } catch (WolkenException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public BlockHeader findCommonAncestor(BlockIndex block) {
        // request block headers
        Message response = context.getServer().broadcastRequest(new RequestHeadersBefore(context.getNetworkParameters().getVersion(), block.getHash(), 1024, block.getBlock()));

        // we store ancestor hashes here
        Set<byte[]> ancestors = new LinkedHashSet<>();

        if (response != null) {
            Collection<BlockHeader> headers = response.getPayload();

            while (headers != null) {
                Iterator<BlockHeader> iterator = headers.iterator();

                BlockHeader header = iterator.next();
                if (isCommonAncestor(header)) {
                    Logger.alert("found common ancestor" + header + " for block" + block);
                    return header;
                } else {
                    if (!header.verifyProofOfWork()) {
                        markRejected(header.getHashCode());
                        for (byte[] hash : ancestors) {
                            markRejected(hash);
                        }
                        markRejected(block.getHash());
                        return null;
                    }

                    ancestors.add(header.getHashCode());
                }

                // loop headers to find a common ancestor
                while (iterator.hasNext()) {
                    header = iterator.next();

                    if (isCommonAncestor(header)) {
                        Logger.alert("found common ancestor" + header + " for block" + block);
                        return header;
                    }
                }

                // find older ancestor
                response = context.getServer().broadcastRequest(new RequestHeadersBefore(context.getNetworkParameters().getVersion(), header.getHashCode(), 4096, header));

                if (response != null) {
                    headers = response.getPayload();
                }
            }
        }

        return null;
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
        mutex.lock();
        try {
            return !orphanedBlocks.isEmpty();
        } finally {
            mutex.unlock();
        }
    }

    private BlockIndex nextOrphan() {
        mutex.lock();
        try {
            return orphanedBlocks.poll();
        } finally {
            mutex.unlock();
        }
    }

    private boolean hasBlocksInPool() {
        mutex.lock();
        try {
            return !blockPool.isEmpty();
        } finally {
            mutex.unlock();
        }
    }

    private BlockIndex nextFromPool() {
        mutex.lock();
        try {
            return blockPool.poll();
        } finally {
            mutex.unlock();
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

    private boolean isCommonAncestor(BlockHeader blockHeader) {
        return context.getDatabase().checkBlockExists(blockHeader.getHashCode());
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

    private boolean rollbackIntoExistingParent(byte[] parentHash, int height) throws WolkenException {
        // check that the block exists
        if (context.getDatabase().checkBlockExists(parentHash)) {
            return true;
        }

        // we must request it in case it doesn't
        BlockIndex parent = requestBlock(parentHash);
        while (parent != null) {
            replaceBlockIndex(height, parent);
            height      --;
            parentHash  = parent.getBlock().getParentHash();

            if (context.getDatabase().checkBlockExists(parentHash) || height == -1) {
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
        context.getDatabase().setBlockIndex(height, block);
    }

    private void replaceBlockIndex(int height, BlockIndex block) {
        BlockIndex previousIndex = context.getDatabase().findBlock(height);
        if (previousIndex != null) {
            addStale(previousIndex);
        }

        context.getDatabase().setBlockIndex(height, block);
    }

    private void deleteBlockIndex(int height, boolean orphan) {
        BlockIndex block = context.getDatabase().findBlock(height);
        deleteBlockIndex(block, orphan);
    }

    private void deleteBlockIndex(BlockIndex block, boolean orphan) {
        if (orphan) {
            addStale(block);
        }

        context.getDatabase().deleteBlock(block.getHeight());
    }

    private BlockIndex requestBlock(byte hash[]) {
        Message request = new RequestBlocks(context.getNetworkParameters().getVersion(), hash);
        Message response= context.getServer().broadcastRequest(request);

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
        context.getDatabase().setTip(block);
        replaceBlockIndex(block.getHeight(), block);
    }

    private void addOrphan(BlockIndex block) {
        mutex.lock();
        try {
            orphanedBlocks.add(block);

            // calculate the maximum blocks allowed in the queue.
            int maximumBlocks   = MaximumOrphanBlockQueueSize / context.getNetworkParameters().getMaxBlockSize();
            int Threshold       = (MaximumOrphanBlockQueueSize / 4) / context.getNetworkParameters().getMaxBlockSize();

            // remove any blocks that are too far back in the queue.
            if (orphanedBlocks.size() - maximumBlocks > Threshold) {
                trimOrphans(maximumBlocks);
            }
        } finally {
            mutex.unlock();
        }
    }

    private void addStale(BlockIndex block) {
        mutex.lock();
        try {
            staleBlocks.add(block);

            // calculate the maximum blocks allowed in the queue.
            int maximumBlocks   = MaximumStaleBlockQueueSize / context.getNetworkParameters().getMaxBlockSize();
            int Threshold       = (MaximumStaleBlockQueueSize / 4) / context.getNetworkParameters().getMaxBlockSize();

            // remove any blocks that are too far back in the queue.
            if (staleBlocks.size() - maximumBlocks > Threshold) {
                trimStales(maximumBlocks);
            }
        } finally {
            mutex.unlock();
        }
    }

    private void markRejected(byte block[]) {
        context.getDatabase().markRejected(block);
    }

    public void pool(BlockIndex block) {
        mutex.lock();
        try {
            blockPool.add(block);

            // calculate the maximum blocks allowed in the queue.
            int maximumBlocks   = MaximumPoolBlockQueueSize / context.getNetworkParameters().getMaxBlockSize();
            int threshold       = (MaximumPoolBlockQueueSize / 4) / context.getNetworkParameters().getMaxBlockSize();

            // remove any blocks that are too far back in the queue.
            if (blockPool.size() - maximumBlocks > threshold) {
                trimPool(maximumBlocks);
            }
        } finally {
            mutex.unlock();
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
        Block genesis = new Block(new byte[Block.UniqueIdentifierLength], context.getNetworkParameters().getDefaultBits());
        genesis.addTransaction(Transaction.newMintTransaction("", context.getNetworkParameters().getMaxReward(), context.getNetworkParameters().getFoundingAddresses()));
        genesis.setNonce(0);
        return new BlockIndex(genesis, BigInteger.ZERO, 0);
    }

    public BlockIndex makeBlock() throws WolkenException {
        mutex.lock();
        try {
            return tip.generateNextBlock();
        }
        finally {
            mutex.unlock();
        }
    }

    public BlockIndex getTip() {
        mutex.lock();
        try {
            return tip;
        }
        finally {
            mutex.unlock();
        }
    }

    public boolean contains(byte[] hash) {
        mutex.lock();
        try {
            return orphanedBlocks.containsKey(hash) || staleBlocks.containsKey(hash) || blockPool.containsKey(hash);
        }
        finally {
            mutex.unlock();
        }
    }

    public Queue<BlockIndex> getOrphanedBlocks() {
        mutex.lock();
        try {
            return new PriorityQueue<>(orphanedBlocks);
        }
        finally {
            mutex.unlock();
        }
    }

    public BlockIndex getBlock(byte[] hash) {
        mutex.lock();
        try {
            return orphanedBlocks.getByHash(hash);
        }
        finally {
            mutex.unlock();
        }
    }

    public Set<byte[]> getInv() {
        mutex.lock();
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
            mutex.unlock();
        }
    }

    public void suggest(Set<BlockIndex> blocks) {
        for (BlockIndex block : blocks) {
            pool(block);
        }
    }

    public void suggest(BlockIndex block) {
        if (!isRejected(block.getHash())) {
            pool(block);
        }
    }

    private boolean isRejected(byte[] hash) {
        return context.getDatabase().isRejected(hash);
    }

    public int getHeight() {
        BlockIndex tip = getTip();
        if (tip != null) {
            return tip.getHeight();
        }

        return 0;
    }
}
