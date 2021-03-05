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
import java.util.*;

public class RequestHeadersBefore extends Message {
    private byte        hash[];
    private int         count;

    // not serialized
    private BlockHeader checkAgainst;

    public RequestHeadersBefore(int version, byte hash[], int count, BlockHeader checkAgainst) {
        super(version, Flags.Request);
        this.hash   = hash;
        this.count  = count;
        this.checkAgainst = checkAgainst;
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
            BlockHeader header      = Context.getInstance().getDatabase().findBlockHeader(i);
            // database internal error
            // this should not happen
            if (header == null) {
                node.sendMessage(new FailedToRespondMessage(Context.getInstance().getNetworkParameters().getVersion(), FailedToRespondMessage.ReasonFlags.CouldNotFindRequestedData, getUniqueMessageIdentifier()));
                return;
            }
        }

        headers.add(Context.getInstance().getDatabase().findBlockHeader(hash));

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
                return ResponseMetadata.ValidationBits.InvalidResponse | ResponseMetadata.ValidationBits.SpamfulResponse;
            }

            int response    = 0;
            Collection<BlockHeader> headers = msg.getPayload();
            if (headers.size() > (count + 1) || headers.isEmpty()) {
                response |= ResponseMetadata.ValidationBits.SpamfulResponse;
                response |= ResponseMetadata.ValidationBits.InvalidResponse;
            }

            Iterator<BlockHeader> iterator = new ArrayList<>(headers).iterator();
            if (iterator.hasNext()) {
                BlockHeader header = iterator.next();

                while (iterator.hasNext()) {
                    BlockHeader next = iterator.next();

                    if (!Utils.equals(header.getHashCode(), next.getParentHash())) {
                        return response | ResponseMetadata.ValidationBits.SpamfulResponse | ResponseMetadata.ValidationBits.InvalidResponse;
                    }

                    if (!iterator.hasNext()) {
                        if (!Utils.equals(header.getHashCode(), hash)) {
                            return response | ResponseMetadata.ValidationBits.SpamfulResponse | ResponseMetadata.ValidationBits.InvalidResponse;
                        }
                    }
                }
            }

            return response;
        };
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RequestHeadersBefore(getVersion(), new byte[Block.UniqueIdentifierLength], 0, new BlockHeader());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(RequestHeadersBefore.class);
    }
}
