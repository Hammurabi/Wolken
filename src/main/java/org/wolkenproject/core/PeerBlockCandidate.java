package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenTimeoutException;
import org.wolkenproject.network.CheckedResponse;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.messages.Inv;
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
        // get all block headers excluding the most recent common ancestor and verify.
        List<BlockHeader> ancestors = findCommonAncestors(getContext(), sender, header);
        // check that we received the headers.
        if (ancestors == null) return false;
        // get all blocks.
        // propagate.

        Message notify = new Inv(getContext().getNetworkParameters().getVersion(), Inv.Type.Block, header.getHashCode());

        return true;
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

        Stack<Collection<BlockHeader>> ancestorRequests = new Stack<>();

        if (response != null) {
            Collection<BlockHeader> headerCollection = response.getMessage().getPayload();
            List<BlockHeader> headers = new ArrayList<>(headerCollection);

            while (headers != null) {
                int lastCommonAncestor = -1;

                for (int i = 0; i < headers.size(); i ++) {
                    if (isCommonAncestor(context, headers.get(i))) {
                        lastCommonAncestor = i;
                    }

                    if (!headers.get(i).verifyProofOfWork()) {
                        // set the best block as rejected.
                        context.getBlockChain().markRejected(best.getHashCode());
                        // set the current block as rejected.
                        context.getBlockChain().markRejected(headers.get(i).getHashCode());

                        // reject all blocks that come after it.
                        for (int j = i + 1; j < headers.size(); j ++) {
                            context.getBlockChain().markRejected(headers.get(j).getHashCode());
                        }

                        // reject the rest of the blocks we collected.
                        while (!ancestorRequests.isEmpty()) {
                            for (BlockHeader header : ancestorRequests.pop()) {
                                context.getBlockChain().markRejected(header.getHashCode());
                            }
                        }

                        return null;
                    }
                }

                if (lastCommonAncestor != -1) {
                    for (int i = lastCommonAncestor + 1; i < headers.size(); i ++) {
                        ancestors.add(headers.get(i));
                    }

                    while (!ancestorRequests.isEmpty()) {
                        ancestors.addAll(ancestorRequests.pop());
                    }

                    return ancestors;
                }

                // push the header collection into the stack.
                ancestorRequests.push(headers);

                // find older ancestor
                request = new RequestHeadersBefore(context.getNetworkParameters().getVersion(), headers.get(headers.size() - 1).getHashCode(), 4096, headers.get(headers.size() - 1));
                response = null;

                try {
                    response = sender.getResponse(request, context.getNetworkParameters().getMessageTimeout(4096 * BlockHeader.Size));
                    if (!response.noErrors()) {
                        return null;
                    }
                } catch (WolkenTimeoutException e) {
                    e.printStackTrace();
                }

                if (response != null) {
                    headerCollection = response.getMessage().getPayload();
                    headers = new ArrayList<>(headerCollection);
                }
            }
        }

        return null;
    }

    private static boolean isCommonAncestor(Context context, BlockHeader blockHeader) {
        return context.getDatabase().checkBlockExists(blockHeader.getHashCode());
    }
}
