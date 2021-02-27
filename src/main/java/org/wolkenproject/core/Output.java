package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.Utils;

import java.nio.ByteBuffer;

public class Output {
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

    public byte[] asByteArray() {
        return Utils.concatenate(Utils.takeApartLong(value), Utils.takeApart(data.length), data);
    }
}
