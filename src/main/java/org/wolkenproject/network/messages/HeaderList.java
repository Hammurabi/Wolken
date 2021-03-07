package org.wolkenproject.network.messages;

import org.wolkenproject.core.BlockHeader;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;

public class HeaderList extends ResponseMessage {
    private Collection<BlockHeader> headers;

    public HeaderList(int version, Collection<BlockHeader> headers, byte[] uniqueMessageIdentifier) {
        super(version, uniqueMessageIdentifier);
        this.headers = new LinkedHashSet<>(headers);
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(headers.size(), stream);
        for (BlockHeader block : headers) {
            block.write(stream);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
        byte buffer[] = new byte[4];
        stream.read(buffer);
        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i++) {
            try {
                BlockHeader header = Context.getInstance().getSerialFactory().fromStream(Context.getInstance().getSerialFactory().getSerialNumber(BlockHeader.class), stream);
                headers.add(header);
            } catch (WolkenException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public <Type> Type getPayload() {
        return (Type) headers;
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new HeaderList(getVersion(), headers, getUniqueMessageIdentifier());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(HeaderList.class);
    }
}