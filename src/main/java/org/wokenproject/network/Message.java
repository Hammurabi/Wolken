package org.wokenproject.network;

public class Message {
    private int version;
    private int flags;
    private int contentType;
    private int instanceCount;

    public Message(int version, int flags, int type, int count)
    {
        this.version        = version;
        this.flags          = flags;
        this.contentType    = type;
        this.instanceCount  = count;
    }
}