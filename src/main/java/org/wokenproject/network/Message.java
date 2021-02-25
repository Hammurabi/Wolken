package org.wokenproject.network;

import org.wokenproject.utils.HashUtil;
import org.wokenproject.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class Message {
    private int     version;
    private int     flags;
    private int     contentType;
    private int     instanceCount;
    private byte    content[];

    public Message(int version, int flags, int type, int count, byte content[])
    {
        this.version        = version;
        this.flags          = flags;
        this.contentType    = type;
        this.instanceCount  = count;
        this.content        = content;
    }

    public void writeToStream(BufferedOutputStream stream) throws IOException {
        Utils.writeInt(version, stream);
        Utils.writeInt(flags, stream);
        Utils.writeInt(contentType, stream);
        Utils.writeInt(instanceCount, stream);
        Utils.writeInt(content.length, stream);
        stream.write(content);
        stream.flush();
    }

    public byte[] getMessageBytes()
    {
        return HashUtil.hash160(Utils.concatenate(
                Utils.takeApart(version),
                Utils.takeApart(flags),
                Utils.takeApart(contentType),
                Utils.takeApart(instanceCount),
                Utils.takeApart(content.length),
                content
        ));
    }

    public byte[] getUniqueMessageIdentifier()
    {
        return HashUtil.hash160(Utils.concatenate(
                Utils.takeApart(contentType),
                Utils.takeApart(instanceCount),
                content
        ));
    }
}