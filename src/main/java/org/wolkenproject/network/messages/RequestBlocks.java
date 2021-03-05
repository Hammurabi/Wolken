package org.wolkenproject.network.messages;

import org.wolkenproject.core.Block;
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

public class RequestBlocks extends Message {
    private Set<byte[]> blocks;

    public RequestBlocks(int version, Collection<byte[]> blocks) {
        super(version, Flags.Request);
        this.blocks = new LinkedHashSet<>(blocks);
    }

    public RequestBlocks(int version, byte[] hash) {
        this(version, toSet(hash));
    }

    private static Set<byte[]> toSet(byte[] hash) {
        Set<byte[]> set = new LinkedHashSet<>();
        set.add(hash);

        return set;
    }

    @Override
    public void executePayload(Server server, Node node) {
        Set<BlockIndex> blocks = new LinkedHashSet<>();
        for (byte[] hash : this.blocks) {
            BlockIndex block    = Context.getInstance().getDatabase().findBlock(hash);

            if (block != null) {
                blocks.add(block);
            }
        }

        // send the blocks
        node.sendMessage(new BlockList(Context.getInstance().getNetworkParameters().getVersion(), blocks, getUniqueMessageIdentifier()));
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException {
        Utils.writeInt(blocks.size(), stream);
        for (byte[] hash : blocks)
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

            blocks.add(hash);
        }
    }

    @Override
    public <Type> Type getPayload() {
        return (Type) blocks;
    }

    @Override
    public ResponseMetadata getResponseMetadata() {
        return (msg)->{
            boolean isCorrectType = msg instanceof BlockList;

            if (!isCorrectType) {
                return ResponseMetadata.ValidationBits.InvalidResponse;
            }

            int response = 0;
            Collection<BlockIndex> blocks = msg.getPayload();

            int checked = 0;
            for (BlockIndex block : blocks) {
                if (this.blocks.contains(block.getHash())) {
                    checked ++;
                }
            }

            if (blocks.size() > this.blocks.size()) {
                response |= ResponseMetadata.ValidationBits.SpamfulResponse;
                response |= ResponseMetadata.ValidationBits.InvalidResponse;
            }

            if (checked != this.blocks.size()) {
                response |= ResponseMetadata.ValidationBits.PartialResponse;
                response |= ResponseMetadata.ValidationBits.InvalidResponse;
            }

            if (checked == this.blocks.size() && response != 0) {
                response |= ResponseMetadata.ValidationBits.EntireResponse;
            }

            return response;
        };
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RequestBlocks(getVersion(), blocks);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(RequestBlocks.class);
    }
}
