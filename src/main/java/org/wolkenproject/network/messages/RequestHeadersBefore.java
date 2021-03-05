package org.wolkenproject.network.messages;

import org.wolkenproject.core.Block;
import org.wolkenproject.core.BlockHeader;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.ResponseMetadata;
import org.wolkenproject.network.Server;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class RequestHeadersBefore extends Message {
    // not serialized.
    private BlockIndex  block;

    private byte        hash[];
    private int         count;

    public RequestHeadersBefore(int version, BlockIndex block, int count) {
        super(version, Flags.Request);
        this.block  = block;
        this.hash   = block.getHash();
        this.count  = count;
    }

    private static Set<byte[]> toSet(byte[] hash) {
        Set<byte[]> set = new LinkedHashSet<>();
        set.add(hash);

        return set;
    }

    @Override
    public void executePayload(Server server, Node node) {
        Set<BlockHeader> headers    = new LinkedHashSet<>();

        // fetch the block header
        BlockIndex index            = Context.getInstance().getDatabase().findBlock(hash);

        // if it doesn't exist then respond with an error
        if (index == null) {
            node.sendMessage(new FailedToRespondMessage(Context.getInstance().getNetworkParameters().getVersion(), FailedToRespondMessage.ReasonFlags.CouldNotFindRequestedData, getUniqueMessageIdentifier()));
            return;
        }

        int earliestHeader          = Math.max(0, index.getHeight() - count);

        for (int i = earliestHeader; i < index.getHeight(); i ++) {
            BlockHeader header          = Context.getInstance().getDatabase().findBlockHeader(i);
            // database internal error
            // this should not happen
            if (header == null) {
                node.sendMessage(new FailedToRespondMessage(Context.getInstance().getNetworkParameters().getVersion(), FailedToRespondMessage.ReasonFlags.CouldNotFindRequestedData, getUniqueMessageIdentifier()));
                return;
            }
        }

        // send the headers
        node.sendMessage(new HeaderList(Context.getInstance().getNetworkParameters().getVersion(), headers, getUniqueMessageIdentifier()));
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException {
        stream.write(hash);
        Utils.writeInt(count, stream);
    }

    @Override
    public void readContents(InputStream stream) throws IOException {
        stream.read(hash);
        byte buffer[] = new byte[4];
        stream.read(buffer);
        count = Utils.makeInt(buffer);
    }

    @Override
    public <Type> Type getPayload() {
        return null;
    }

    @Override
    public ResponseMetadata getResponseMetadata() {
        return (msg)->{
            boolean isCorrectType = msg instanceof HeaderList;

            if (!isCorrectType) {
                return ResponseMetadata.ValidationBits.InvalidResponse;
            }

            int response = 0;
            Collection<BlockHeader> headers = msg.getPayload();

            int checked = 0;
            for (BlockHeader header : headers) {
                if (this.headers.contains(header.getHashCode())) {
                    checked ++;
                }
            }

            if (headers.size() > this.headers.size()) {
                response |= ResponseMetadata.ValidationBits.SpamfulResponse;
                response |= ResponseMetadata.ValidationBits.InvalidResponse;
            }

            if (checked != this.headers.size()) {
                response |= ResponseMetadata.ValidationBits.PartialResponse;
                response |= ResponseMetadata.ValidationBits.InvalidResponse;
            }

            if (checked == this.headers.size() && response != 0) {
                response |= ResponseMetadata.ValidationBits.EntireResponse;
            }

            return response;
        };
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RequestHeadersBefore(getVersion(), headers);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(RequestHeadersBefore.class);
    }
}
