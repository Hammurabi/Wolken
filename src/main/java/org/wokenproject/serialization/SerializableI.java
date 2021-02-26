package org.wokenproject.serialization;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.utils.HashUtil;
import org.wokenproject.utils.Utils;

import java.io.*;

public abstract class SerializableI {
    public void serialize(OutputStream stream) throws IOException {
        Utils.writeInt(getSerialNumber(), stream);
        byte content[] = asByteArray();
        Utils.writeInt(content.length, stream);
        stream.write(content);
    }

    public abstract void write(OutputStream stream) throws IOException;
    public abstract void read(InputStream stream) throws IOException;

    public byte[] asByteArray() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            write(outputStream);
            outputStream.flush();
            outputStream.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public byte[] asSerializedArray() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            serialize(outputStream);
            outputStream.flush();
            outputStream.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public <Type extends SerializableI> Type makeCopy() throws IOException, WolkenException {
        byte array[] = asSerializedArray();
        BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(array));
        Type t = Context.getInstance().getSerialFactory().fromStream(inputStream);
        inputStream.close();

        return t;
    }

    public abstract <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException;

    public byte[] checksum() throws IOException {
        return HashUtil.hash160(asByteArray());
    }

    public abstract int getSerialNumber();
}