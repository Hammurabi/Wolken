package org.wolkenproject.network.messages;

import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Node;
import org.wolkenproject.network.Server;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlockList extends ResponseMessage {
    private Set<BlockIndex>      blocks;

    public BlockList(int version, Collection<BlockIndex> blocks, byte[] uniqueMessageIdentifier) {
        super(version, uniqueMessageIdentifier);
        this.blocks   = new LinkedHashSet<>(blocks);
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
        VarInt.writeCompactUInt32(blocks.size(), false, stream);
        for (BlockIndex block : blocks)
        {
            block.write(stream);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
        int length = VarInt.readCompactUInt32(false, stream);

        for (int i = 0; i < length; i ++)
        {
            try {
                BlockIndex block = Context.getInstance().getSerialFactory().fromStream(Context.getInstance().getSerialFactory().getSerialNumber(BlockIndex.class), stream);
                blocks.add(block);
            } catch (WolkenException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public <Type> Type getPayload() {
        return (Type) blocks;
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new BlockList(getVersion(), blocks, getUniqueMessageIdentifier());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(BlockList.class);
    }

    @Override
    public void execute(Server server, Node node) {
    }
}
