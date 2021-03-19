package org.wolkenproject.core;

import org.wolkenproject.network.Message;
import org.wolkenproject.network.messages.RequestHeadersBefore;
import org.wolkenproject.utils.Logger;
import org.wolkenproject.utils.PriorityHashQueue;

import java.util.Collection;
import java.util.Iterator;
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
}
