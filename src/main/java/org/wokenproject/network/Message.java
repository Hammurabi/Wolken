package org.wokenproject.network;

import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.HashUtil;
import org.wokenproject.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class Message extends SerializableI {
    public static final class Flags
    {
        public static final int
            None = 0,
            Notify = 1,
            Request = 2,
            Response = 3;
    }

    private int     version;
    private int     flags;

    public Message(int version, int flags)
    {
        this.version        = version;
        this.flags          = flags;
    }

    public abstract void executePayload(Server server, Node node);

    public void writeHeader(OutputStream stream) throws IOException {
        Utils.writeInt(version, stream);
        Utils.writeInt(flags, stream);
        stream.flush();
    }

    public byte[] getUniqueMessageIdentifier()
    {
        return HashUtil.hash160(Utils.concatenate(
                Utils.takeApart(version),
                Utils.takeApart(flags),
                getContents()
        ));
    }

    public int getVersion()
    {
        return version;
    }
    public int getFlags()
    {
        return flags;
    }
    public abstract byte[] getContents();
}