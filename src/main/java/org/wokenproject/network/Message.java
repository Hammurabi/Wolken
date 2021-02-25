package org.wokenproject.network;

import org.wokenproject.utils.HashUtil;
import org.wokenproject.utils.Utils;

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

    public byte[] contentHash()
    {
        return HashUtil.hash160(Utils.concatenate(
                Utils.takeApart(contentType),
                Utils.takeApart(instanceCount),
                content
        ));
    }
}