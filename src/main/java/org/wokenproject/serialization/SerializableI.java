package org.wokenproject.serialization;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.InvalidSerialNumberException;

import java.io.*;

public abstract class SerializableI {
    public abstract void write(OutputStream stream);
    public abstract void read(InputStream stream);

    public boolean hasPayload()
    {
        return getPayloadMetadata() != null;
    }

    public abstract Metadata getPayloadMetadata();

    public <Type extends SerializableI> Type executePayload() {
        return executePayload(0);
    }

    public abstract <Type extends SerializableI> Type executePayload(int funcPtr, Object... args);


    public byte[] asByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        write(outputStream);
        outputStream.flush();
        outputStream.close();

        return outputStream.toByteArray();
    }

    public <Type extends SerializableI> Type makeCopy() throws IOException, InvalidSerialNumberException {
        byte array[] = asByteArray();
        BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(array));
        Type t = Context.getInstance().getSerialFactory().fromStream(inputStream);
        inputStream.close();

        return t;
    }

    public abstract <Type extends SerializableI> Type newInstance(Object ...object);
}
