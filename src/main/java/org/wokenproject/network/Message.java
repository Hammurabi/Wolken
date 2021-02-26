package org.wokenproject.network;

import org.wokenproject.serialization.SerializableI;
import org.wokenproject.utils.HashUtil;
import org.wokenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Message extends SerializableI {
    public static final class Flags {
        public static final int
                None = 0,
                Notify = 1,
                Request = 2,
                Response = 3;
    }

    private int version;
    private int flags;

    public Message(int version, int flags) {
        this.version = version;
        this.flags = flags;
    }

    public abstract void executePayload(Server server, Node node);

    public void writeHeader(OutputStream stream) throws IOException {
        Utils.writeInt(version, stream);
        Utils.writeInt(flags, stream);
        stream.flush();
    }

    public void readHeader(InputStream stream) throws IOException {
        byte buffer[] = new byte[4];
        stream.read(buffer);
        version = Utils.makeInt(buffer);
        stream.read(buffer);
        flags = Utils.makeInt(buffer);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        writeHeader(stream);
        writeContents(stream);
    }

    public abstract void read(InputStream stream) throws IOException;

    public byte[] getUniqueMessageIdentifier() {
        return HashUtil.hash160(asByteArray());
    }

    public int getVersion() {
        return version;
    }

    public int getFlags() {
        return flags;
    }

    public abstract void writeContents(OutputStream stream) throws IOException;

    public abstract void readContents(InputStream stream) throws IOException;
}