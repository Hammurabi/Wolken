package org.wokenproject.network;

import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.HashUtil;
import org.wokenproject.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.IOException;

public abstract class Message extends SerializableI {
    public static final class Flags
    {
        public static final int
        NONE = 0,
        NOTIFY = 1,
        REQUEST = 2,
        RESPONSE = 3;
    }

    private int     version;
    private int     flags;

    public Message(int version, int flags)
    {
        this.version        = version;
        this.flags          = flags;
    }

    public abstract void executePayload(Server server, Node node);

    public void writeHeader(BufferedOutputStream stream) throws IOException {
        Utils.writeInt(version, stream);
        Utils.writeInt(flags, stream);
        stream.flush();
    }

    public byte[] getMessageBytes()
    {
        return HashUtil.hash160(Utils.concatenate(
                Utils.takeApart(version),
                Utils.takeApart(flags)
        ));
    }

    public abstract byte[] getUniqueMessageIdentifier();

    public int getVersion()
    {
        return version;
    }

    public int getFlags()
    {
        return flags;
    }
}