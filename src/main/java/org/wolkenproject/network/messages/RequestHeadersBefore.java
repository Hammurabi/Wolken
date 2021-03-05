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
        BlockHeader header          = Context.getInstance().getDatabase().findBlockHeader(hash);

        // if it doesn't exist then respond with an error
        if (header == null) {
            node.sendMessage(new FailedToRespondMessage(Context.getInstance().getNetworkParameters().getVersion(), FailedToRespondMessage.ReasonFlags.CouldNotFindRequestedData, getUniqueMessageIdentifier()));
            return;
        }



        for (byte[] hash : this.headers) {
            BlockHeader header  = Context.getInstance().getDatabase().findBlockHeader(hash);

            if (header != null) {
                headers.add(header);
            }
        }

        // send the headers
        node.sendMessage(new HeaderList(Context.getInstance().getNetworkParameters().getVersion(), headers, getUniqueMessageIdentifier()));
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException {
        Utils.writeInt(headers.size(), stream);
        for (byte[] hash : headers)
        {
            stream.write(hash);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException {
        byte buffer[] = new byte[4];
        stream.read(buffer);

        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i ++)
        {
            byte hash[] = new byte[Block.UniqueIdentifierLength];
            stream.read(hash);

            headers.add(hash);
        }
    }

    @Override
    public <Type> Type getPayload() {
        return (Type) headers;
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
