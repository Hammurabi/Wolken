package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Output extends SerializableI {
    private long value;
    private byte data[];

    public Output(long value, byte data[])
    {
        this.value  = value;
        this.data   = data;
    }

    public Output(byte[] data) throws WolkenException {
        if (data == null)
        {
            throw new WolkenException("null byte array provided.");
        }

        if (data.length < 12)
        {
            throw new WolkenException("invalid byte array provided.");
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.value = buffer.getLong();
        int length = buffer.getInt();
        this.data = new byte[length];
        buffer.get(this.data);
    }

    public long getValue()
    {
        return value;
    }

    public byte[] getData()
    {
        return data;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        Utils.writeLong(value, stream);
        Utils.writeUnsignedInt16(data.length, stream);
        stream.write(data);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        byte buffer[]   = new byte[8];
        stream.read(buffer);
        this.value      = Utils.makeLong(buffer);
        this.data       = new byte[Utils.makeInt((byte) 0, (byte) 0, buffer[0], buffer[1])];
    }

    public byte[] asByteArray() {
        return Utils.concatenate(Utils.takeApartLong(value), Utils.takeApart(data.length), data);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return null;
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Output.class);
    }
}
