package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenTimeoutException;
import org.wolkenproject.network.CheckedResponse;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.messages.RequestBlocks;

public class PeerBlockCandidate extends CandidateBlock {
    private BlockHeader header;
    private BlockIndex  block;
    private Node        sender;

    public PeerBlockCandidate(Node sender, BlockHeader header) {
        this.header = header;
    }

    @Override
    public BlockHeader getBlockHeader() {
        return header;
    }

    @Override
    public BlockIndex getBlock() {
        if (block == null) {
            Message request = new RequestBlocks(sender.getVersionInfo().getVersion(), header.getHashCode());
            try {
                CheckedResponse response = sender.getResponse(request, Context.getInstance().getNetworkParameters().getMessageTimeout());
            } catch (WolkenTimeoutException e) {
                return null;
            }
        }

        return null;
    }
}
