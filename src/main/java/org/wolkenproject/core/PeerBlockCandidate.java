package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenTimeoutException;
import org.wolkenproject.network.CheckedResponse;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.messages.Inv;
import org.wolkenproject.network.messages.RequestBlocks;
import org.wolkenproject.network.messages.RequestHeadersBefore;

import java.math.BigInteger;
import java.util.*;

public class PeerBlockCandidate extends CandidateBlock {
    private List<BlockHeader>   chain;
    private BlockHeader         header;
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
        chain = ancestors;
        chain.add(header);
        // get all blocks.
        if (!downloadAndVerifyBlocks()) return false;
        // propagate.
        Message notify = new Inv(getContext().getNetworkParameters().getVersion(), Inv.Type.Block, header.getHashCode());
        getContext().getServer().broadcast(notify, sender);

        return true;
    }

    @Override
    public void merge(AbstractBlockChain target) {
        byte mostRecentCommonAncestor[] = chain.get(chain.size() - 1).getParentHash();
        BlockMetadata commonAncestor    = getContext().getDatabase().findBlockMetaData(mostRecentCommonAncestor);
        int height                      = commonAncestor.getHeight();
        BigInteger work                 = commonAncestor.getPreviousChainWork().add(commonAncestor.getBlockHeader().getWork());

        target.staleBlock(mostRecentCommonAncestor);

        for (BlockHeader header : chain) {
            // get the block from temp storage.
            Block block = getContext().getDatabase().findTempBlock(header.getHashCode());
            // delete the block from temp storage.
            getContext().getDatabase().deleteTempBlock(header.getHashCode());
            // set the block to the new block index.
            target.setBlock(++ height, new BlockIndex(block, new BlockMetadata(header, height, block.getTransactionCount(), block.getEventCount(), block.getTotalValue(), block.getFees(), work)));
            // add the block's work to the total work.
            work = work.add(block.getWork());
        }
    }

    private boolean downloadAndVerifyBlocks() {
        // parent metadata.
        BlockMetadata metadata  = getContext().getDatabase().findBlockMetaData(chain.get(chain.size() - 1).getParentHash());

        // get the height.
        int height              = metadata.getHeight();

        // this should give us 16 blocks per message at (4mb).
        int blocksPerMessage    = (getContext().getNetworkParameters().getMaxMessageContentSize() / getContext().getNetworkParameters().getMaxBlockSize()) / 2;

        BlockHeader parent      = metadata.getBlockHeader();

        // loop all headers and download the blocks.
        for (int i = 0; i < chain.size(); i += blocksPerMessage) {
            List<byte[]> blocks = new ArrayList<>();
            for (int j = 0; j < blocksPerMessage; j ++) {
                blocks.add(chain.get(i + j).getHashCode());
            }

            Message request = new RequestBlocks(getContext().getNetworkParameters().getVersion(), blocks);
            CheckedResponse response = null;

            try {
                response = sender.getResponse(request, getContext().getNetworkParameters().getMessageTimeout(blocksPerMessage * getContext().getNetworkParameters().getMaxBlockSize()));
                if (response.noErrors()) {
                    Collection<Block> bl = response.getMessage().getPayload();
                    int j = 0;
                    for (Block block : bl) {
                        if (block.verify(parent, ++height)) {
                            getContext().getDatabase().tempStoreBlock(block);
                            parent = block.getBlockHeader();
                        } else {
                            invalidate(getContext(), i + j, chain);
                            closeConnection();
                            return false;
                        }
                    }
                } else {
                    closeConnection();
                    return false;
                }
            } catch (WolkenTimeoutException e) {
                return false;
            }
        }

        return true;
    }

    private void invalidate(Context context, int inclusiveIndex, List<BlockHeader> chain) {
        for (int i = inclusiveIndex; i < chain.size(); i ++) {
            context.getDatabase().markRejected(chain.get(i).getHashCode());
        }
    }

    @Override
    public List<BlockHeader> getChain() {
        return chain;
    }

    @Override
    public boolean isChainAvailable() {
        return chain != null;
    }

    @Override
    public boolean destroy() {
        for (BlockHeader header : chain) {
            getContext().getDatabase().deleteTempBlock(header.getHashCode());
        }

        chain.clear();
        return true;
    }

    private void closeConnection() {
        try {
            sender.close();
        } catch (Exception e) {
        }
    }

    public static List<BlockHeader> findCommonAncestors(Context context, Node sender, BlockHeader best) {
        List<BlockHeader> ancestors = new ArrayList<>();

        if (context.getDatabase().checkBlockExists(best.getParentHash())) {
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
