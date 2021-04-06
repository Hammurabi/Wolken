package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenTimeoutException;
import org.wolkenproject.network.CheckedResponse;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.messages.RequestBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
}
