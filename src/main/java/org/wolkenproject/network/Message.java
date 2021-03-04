package org.wolkenproject.network;

import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Collection;

public abstract class Message extends SerializableI {
    public static final int UniqueIdentifierLength = 20;

    public static final class Flags {
        public static final int
                None = 0,
                Notify = 1,
                Request = 2,
                Response = 4;
    }

    private int     version;
    private int     flags;
    private long    nonce;

    public Message(int version, int flags) {
        this(version, flags, new SecureRandom().nextLong());
    }

    public Message(int version, int flags, long nonce) {
        this.version    = version;
        this.flags      = flags;
        this.nonce      = nonce;
    }

    public abstract void executePayload(Server server, Node node);

    public void writeHeader(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(version, stream);
        Utils.writeInt(flags, stream);
//        Utils.writeLong(nonce, stream);
        stream.flush();
    }

    public void readHeader(InputStream stream) throws IOException, WolkenException {
        byte buffer[] = new byte[8];
        stream.read(buffer, 0, 4);
        version = Utils.makeInt(buffer);
        stream.read(buffer, 0, 4);
        flags = Utils.makeInt(buffer);
//        stream.read(buffer);
//        nonce = Utils.makeLong(buffer);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        writeHeader(stream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeContents(byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();

        byte bytes[] = byteArrayOutputStream.toByteArray();
        Utils.writeInt(bytes.length, stream);

        stream.write(bytes);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        readHeader(stream);
        byte buffer[] = new byte[4];
        stream.read(buffer);
        int length = Utils.makeInt(buffer);
        if (length > Context.getInstance().getNetworkParameters().getMaxMessageContentSize())
        {
            throw new WolkenException("message content exceeds the maximum size allowed by the protocol.");
        }
        readContents(stream);
    }

    public byte[] getUniqueMessageIdentifier() {
        return HashUtil.hash160(asByteArray());
    }

    public int getVersion() {
        return version;
    }

    public int getFlags() {
        return flags;
    }

//    public long getNonce() {
//        return nonce;
//    }

    public abstract void writeContents(OutputStream stream) throws IOException, WolkenException;
    public abstract void readContents(InputStream stream) throws IOException, WolkenException;

    public abstract <Type> Type getPayload();
}