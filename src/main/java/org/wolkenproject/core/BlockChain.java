package org.wolkenproject.core;

import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.messages.*;
import org.wolkenproject.utils.*;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain extends AbstractBlockChain {
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

    public BlockChain(Context context) {
        super(context);
        orphanedBlocks  = new PriorityHashQueue<>(BlockIndex.class);
        staleBlocks     = new PriorityHashQueue<>(BlockIndex.class);
        blockPool       = new PriorityHashQueue<>(BlockIndex.class);
        tip             = getContext().getDatabase().findTip();
    }

    @Override
    public boolean verifyBlock(BlockIndex block) {
        return false;
    }

    @Override
    public void run() {
        long lastBroadcast  = System.currentTimeMillis();
        byte lastHash[]     = null;

        Logger.alert("attempting to reload chain from last checkpoint.");
        getMutex().lock();
        try {
            tip = getContext().getDatabase().findTip();
            if (tip != null) {
                Logger.alert("loaded checkpoint successfully", tip);
            } else {
                setTip(makeGenesisBlock());
                Logger.alert("loaded genesis as checkpoint successfully", tip);
            }
        } finally {
            getMutex().unlock();
        }

        while (getContext().isRunning()) {
            if (System.currentTimeMillis() - lastBroadcast > (5 * 60_000L)) {
                Set<byte[]> hashCodes = new LinkedHashSet<>();
                BlockIndex tip = getTip();
                hashCodes.add(tip.getHash());

                for (int i = 0; i < 10; i ++) {
                    BlockIndex parent = tip.previousBlock();
                    if (parent == null) {
                        break;
                    }

                    hashCodes.add(parent.getHash());
                }

                try {
                    getContext().getServer().broadcast(new Inv(getContext().getNetworkParameters().getVersion(), Inv.Type.Block, hashCodes));
                } catch (WolkenException e) {
                    e.printStackTrace();
                }
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

                    if (block.getTotalChainWork().compareTo(currentTip.getTotalChainWork()) > 0) {
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

                        Logger.alert("current chain height ${h}", getTip().getHeight());
                        Logger.alert("best block currently set to ${b}", Base16.encode(getTip().getHash()));
                    }
                } catch (WolkenException e) {
                    e.printStackTrace();
                }
            }

            byte tipHash[] = getTip().getHash();

            // everytime the tip hash changes, broadcast it to connected nodes.
            if (!Utils.equals(tipHash, Null.notNull(lastHash))) {
                Set<byte[]> hashCodes = new LinkedHashSet<>();
                hashCodes.add(tipHash);
                lastHash = tipHash;

                try {
                    getContext().getServer().broadcast(new Inv(getContext().getNetworkParameters().getVersion(), Inv.Type.Block, hashCodes));
                } catch (WolkenException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public BlockHeader findCommonAncestor(BlockIndex block) {
        // request block headers
        Message response = getContext().getServer().broadcastRequest(new RequestHeadersBefore(getContext().getNetworkParameters().getVersion(), block.getHash(), 1024, block.getHeader()));

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
                response = getContext().getServer().broadcastRequest(new RequestHeadersBefore(getContext().getNetworkParameters().getVersion(), header.getHashCode(), 4096, header));

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
            if (isRejected(block.getHash())) {
                addOrphan(block);
            }
        }
    }

    private void setNextGapped(BlockIndex block) throws WolkenException {
        BlockHeader commonAncestor = findCommonAncestor(block);

        if (commonAncestor != null) {
            setTip(block);
            rollbackIntoExistingParent(block.getBlock().getParentHash(), block.getHeight() - 1);
        } else {
            if (!isRejected(block.getHash())) {
                addOrphan(block);
            }
        }
    }

    private boolean hasOrphans() {
        getMutex().lock();
        try {
            return !orphanedBlocks.isEmpty();
        } finally {
            getMutex().unlock();
        }
    }

    private BlockIndex nextOrphan() {
        getMutex().lock();
        try {
            return orphanedBlocks.poll();
        } finally {
            getMutex().unlock();
        }
    }

    private boolean hasBlocksInPool() {
        getMutex().lock();
        try {
            return !blockPool.isEmpty();
        } finally {
            getMutex().unlock();
        }
    }

    private BlockIndex nextFromPool() {
        getMutex().lock();
        try {
            return blockPool.poll();
        } finally {
            getMutex().unlock();
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
        return getContext().getDatabase().checkBlockExists(blockHeader.getHashCode());
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

            if (!rollbackIntoExistingParent(block.getBlock().getParentHash(), block.getHeight() - 1)) {
                if (!isRejected(block.getBlock().getParentHash())) {
                    addOrphan(block);
                } else {
                    markRejected(block.getHash());
                }
            }
        } else {
            if (!isRejected(block.getHash())) {
                addOrphan(block);
            }
        }
    }

    private boolean rollbackIntoExistingParent(byte[] parentHash, int height) throws WolkenException {
        // check that the block exists
        if (getContext().getDatabase().checkBlockExists(parentHash)) {
            return true;
        }

        // a set containing children of the parent we are looping.
        Set<byte[]> children = new LinkedHashSet<>();

        // we must request it if it doesn't exist
        BlockIndex parent = requestBlock(parentHash);
        while (parent != null) {
            // if the block is invalid then we reject it and all of it's children
            if (!parent.verify()) {
                markRejected(parentHash);
                for (byte[] hash : children) {
                    markRejected(hash);
                }

                return false;
            } else {
                children.add(parent.getHash());
            }

            // replace the ancestor
            replaceBlockIndex(height, parent);

            height      --;
            parentHash  = parent.getBlock().getParentHash();

            if (getContext().getDatabase().checkBlockExists(parentHash) || height == -1) {
                updateIndices(parent);
                return true;
            }

            // if we can't find the block then again we have to request it
            parent = requestBlock(parentHash);
        }

        return false;
    }

    private void updateIndices(BlockIndex index) throws WolkenException {
        index.recalculateChainWork();
    }

    private void replaceBlockIndex(int height, BlockIndex block) {
        BlockIndex previousIndex = getContext().getDatabase().findBlock(height);
        if (previousIndex != null) {
            addStale(previousIndex);
        }

        getContext().getDatabase().storeBlock(height, block);
    }

    private void deleteBlockIndex(int height, boolean orphan) {
        BlockIndex block = getContext().getDatabase().findBlock(height);
        deleteBlockIndex(block, orphan);
    }

    private void deleteBlockIndex(BlockIndex block, boolean orphan) {
        if (orphan) {
            addStale(block);
        }

        getContext().getDatabase().deleteBlock(block.getHeight());
    }

    private BlockIndex requestBlock(byte hash[]) {
        Message request = new RequestBlocks(getContext().getNetworkParameters().getVersion(), hash);
        Message response= getContext().getServer().broadcastRequest(request);

        if (response instanceof BlockList) {
            Collection<BlockIndex> blocks = response.getPayload();
            if (blocks != null && !blocks.isEmpty()) {
                blocks.iterator().next();
            }
        }

        return null;
    }

    private void setTip(BlockIndex block) {
        tip = block;
        getContext().getDatabase().setTip(block);
        replaceBlockIndex(block.getHeight(), block);
    }

    private void addOrphan(BlockIndex block) {
        getMutex().lock();
        try {
            orphanedBlocks.add(block);

            // calculate the maximum blocks allowed in the queue.
            int maximumBlocks   = MaximumOrphanBlockQueueSize / getContext().getNetworkParameters().getMaxBlockSize();
            int Threshold       = (MaximumOrphanBlockQueueSize / 4) / getContext().getNetworkParameters().getMaxBlockSize();

            // remove any blocks that are too far back in the queue.
            if (orphanedBlocks.size() - maximumBlocks > Threshold) {
                trimOrphans(maximumBlocks);
            }
        } finally {
            getMutex().unlock();
        }
    }

    private void addStale(BlockIndex block) {
        getMutex().lock();
        try {
            staleBlocks.add(block);

            // calculate the maximum blocks allowed in the queue.
            int maximumBlocks   = MaximumStaleBlockQueueSize / getContext().getNetworkParameters().getMaxBlockSize();
            int Threshold       = (MaximumStaleBlockQueueSize / 4) / getContext().getNetworkParameters().getMaxBlockSize();

            // remove any blocks that are too far back in the queue.
            if (staleBlocks.size() - maximumBlocks > Threshold) {
                trimStales(maximumBlocks);
            }
        } finally {
            getMutex().unlock();
        }
    }

    private void markRejected(byte block[]) {
        getContext().getDatabase().markRejected(block);
    }

    public void pool(BlockIndex block) {
        getMutex().lock();
        try {
            blockPool.add(block);

            // calculate the maximum blocks allowed in the queue.
            int maximumBlocks   = MaximumPoolBlockQueueSize / getContext().getNetworkParameters().getMaxBlockSize();
            int threshold       = (MaximumPoolBlockQueueSize / 4) / getContext().getNetworkParameters().getMaxBlockSize();

            // remove any blocks that are too far back in the queue.
            if (blockPool.size() - maximumBlocks > threshold) {
                trimPool(maximumBlocks);
            }
        } finally {
            getMutex().unlock();
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
        Block genesis = new Block(new byte[Block.UniqueIdentifierLength], getContext().getNetworkParameters().getDefaultBits());
        genesis.addTransaction(Transaction.newMintTransaction("", getContext().getNetworkParameters().getMaxReward(), getContext().getNetworkParameters().getFoundingAddresses()));
        genesis.setNonce(0);
        return new BlockIndex(genesis, BigInteger.ZERO, 0);
    }

    public BlockIndex makeBlock() throws WolkenException {
        getMutex().lock();
        try {
            return tip.generateNextBlock();
        }
        finally {
            getMutex().unlock();
        }
    }

    public BlockIndex getTip() {
        getMutex().lock();
        try {
            return tip;
        }
        finally {
            getMutex().unlock();
        }
    }

    public boolean contains(byte[] hash) {
        getMutex().lock();
        try {
            return orphanedBlocks.containsKey(hash) || staleBlocks.containsKey(hash) || blockPool.containsKey(hash);
        }
        finally {
            getMutex().unlock();
        }
    }

    public Queue<BlockIndex> getOrphanedBlocks() {
        getMutex().lock();
        try {
            return new PriorityQueue<>(orphanedBlocks);
        }
        finally {
            getMutex().unlock();
        }
    }

    public BlockIndex getBlock(byte[] hash) {
        getMutex().lock();
        try {
            return orphanedBlocks.getByHash(hash);
        }
        finally {
            getMutex().unlock();
        }
    }

    public Set<byte[]> getInv() {
        getMutex().lock();
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
            getMutex().unlock();
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
        return getContext().getDatabase().isRejected(hash);
    }

    public int getHeight() {
        BlockIndex tip = getTip();
        if (tip != null) {
            return tip.getHeight();
        }

        return 0;
    }

    public BlockIndex createNextBlock() throws WolkenException {
        return getTip().generateNextBlock();
    }
}
