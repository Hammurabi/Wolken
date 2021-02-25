package org.wokenproject.serialization;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.InvalidSerialNumberException;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.utils.HashUtil;

import java.io.*;

public abstract class SerializableI {
    public abstract void write(OutputStream stream) throws IOException;
    public abstract void read(InputStream stream) throws IOException;

    public byte[] asByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        write(outputStream);
        outputStream.flush();
        outputStream.close();

        return outputStream.toByteArray();
    }

    public <Type extends SerializableI> Type makeCopy() throws IOException, WolkenException {
        byte array[] = asByteArray();
        BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(array));
        Type t = Context.getInstance().getSerialFactory().fromStream(inputStream);
        inputStream.close();

        return t;
    }

    public abstract <Type extends SerializableI> Type newInstance(Object ...object) throws WolkenException;

    public byte[] checksum() throws IOException {
        return HashUtil.hash160(asByteArray());
    }
}
