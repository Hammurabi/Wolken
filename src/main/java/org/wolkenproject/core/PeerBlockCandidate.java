package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenTimeoutException;
import org.wolkenproject.network.CheckedResponse;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.messages.RequestBlocks;
import org.wolkenproject.network.messages.RequestHeadersBefore;
import org.wolkenproject.utils.Logger;

import java.util.*;

public class PeerBlockCandidate extends CandidateBlock {
    private List<BlockHeader>   chain;
    private BlockHeader         header;
    private BlockIndex          block;
    private Node                sender;

    public PeerBlockCandidate(Context context, Node sender, BlockHeader header) {
        super(context);
        this.chain  = new ArrayList<>();
        this.sender = sender;
        this.header = header;
    }

    @Override
    public boolean verify() {
        // verify the header.
        if (!header.verifyProofOfWork()) return false;
        // get all block headers.
        List<BlockHeader> ancestors = findCommonAncestors(getContext(), sender, header);
        // check that we received the headers.
        if (ancestors == null) return false;
        // loop all the headers less the best block.
        for (int i = 0; i < ancestors.size() - 1; i ++) {
            if (!ancestors.get(i).verifyProofOfWork()) return false;
        }
        // verify all headers.
        // get all blocks.
        // verify all blocks.
        // propagate.
    }

    @Override
    public BlockHeader getBlockHeader() {
        return header;
    }

    @Override
    public boolean isFullBlockAvailable() {
        if (block == null) {
            Message request = new RequestBlocks(sender.getVersionInfo().getVersion(), header.getHashCode());
            try {
                CheckedResponse response = sender.getResponse(request, Context.getInstance().getNetworkParameters().getMessageTimeout());
                if (response.noErrors()) {
                    Set<BlockIndex> blocks = response.getMessage().getPayload();
                    BlockIndex received = blocks.iterator().next();

                    if (Arrays.equals(block.getHash(), header.getHashCode())) {
                        block = received;

                        return true;
                    } else {
                        closeConnection();
                    }
                } else {
                    closeConnection();
                }
            } catch (WolkenTimeoutException e) {
                closeConnection();
            }
        }

        return false;
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
    public boolean areBlocksAvailable() {
        return false;
    }

    @Override
    public boolean destroy() {
        return false;
    }

    private void closeConnection() {
        try {
            sender.close();
        } catch (Exception e) {
        }
    }

    @Override
    public BlockIndex getBlock() {
        return block;
    }

    public static List<BlockHeader> findCommonAncestors(Context context, Node sender, BlockHeader best) {
        List<BlockHeader> ancestors = new ArrayList<>();

        if (context.getDatabase().checkBlockExists(best.getParentHash())) {
            ancestors.add(best);
            return ancestors;
        }

        // request block headers
        Message request = new RequestHeadersBefore(context.getNetworkParameters().getVersion(), best.getHashCode(), 1024, best);
        CheckedResponse response = null;
        try {
            response = sender.getResponse(request, context.getNetworkParameters().getMessageTimeout(1024 * BlockHeader.Size));
            if (!response.noErrors()) {
                return null;
            }
        } catch (WolkenTimeoutException e) {
            e.printStackTrace();
        }

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
}
