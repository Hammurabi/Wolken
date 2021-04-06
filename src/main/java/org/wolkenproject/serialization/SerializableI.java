package org.wolkenproject.serialization;

import org.wolkenproject.core.Block;
import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VarInt;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public abstract class SerializableI {
    // this function gets called when the Serializable object is serialized locally
    public void serialize(OutputStream stream) throws IOException, WolkenException {
        VarInt.writeCompactUInt32(getSerialNumber(), false, stream);
        write(stream);
    }

    // this function gets called when the Serializable object is sent over network
    public void serializeOverNetwork(OutputStream stream) throws IOException, WolkenException {
        serialize(stream);
    }

    // this function gets called when the Serializable object is sent over network
    public void sendOverNetwork(OutputStream stream) throws IOException, WolkenException {
        write(stream);
    }

    public abstract void write(OutputStream stream) throws IOException, WolkenException;
    public abstract void read(InputStream stream) throws IOException, WolkenException;

    public void networkWrite(OutputStream stream) throws IOException, WolkenException {
        Class c = getClass();
        Field fields[] = c.getFields();
        Set<Field> serializableFields = new LinkedHashSet<>();

        try {
            for (Field field : fields) {
                if (field.isAnnotationPresent(Serializable.class)) {
                    Serializable serializable = field.getAnnotation(Serializable.class);

                    if (serializable.policy() != SerializationPolicy.All && serializable.policy() != SerializationPolicy.NetworkOnly) {
                        continue;
                    }

                    FieldType type = serializable.net() == FieldType.base ? serializable.type() : serializable.net();

                    networkWrite(stream, field, type);
                }
            }
        } catch (IllegalAccessException e) {
            throw new WolkenException(e);
        }
    }

    private final void networkWrite(OutputStream stream, Field field, FieldType type) throws IllegalAccessException, IOException {
        field.setAccessible(true);

        switch (type) {
            case int8:
                stream.write(field.getByte(this));
                return;
            case int16:
                Utils.writeShort(field.getShort(this), stream);
                return;
            case int32:
                Utils.writeInt(field.getInt(this), stream);
                return;
            case int64:
                Utils.writeLong(field.getLong(this), stream);
                return;
            case int128:
            case int256:

            case uint8:
            case uint16:
            case uint32:
            case uint64:
            case uint128:
            case uint256:

            case var8ui:
            case var16ui:
            case var32ui:
            case var64ui:
            case var128ui:
            case var256ui:

            case bytes:
            case hash160:
            case hash256:
        }
    }

    public void networkRead(InputStream stream) throws IOException, WolkenException {
    }

    public <T> T fromBytes(byte bytes[]) throws IOException, WolkenException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        read(inputStream);
        return (T) this;
    }

    public byte[] asByteArray() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            write(outputStream);
            outputStream.flush();
            outputStream.close();

            return outputStream.toByteArray();
        } catch (IOException | WolkenException e) {
            return null;
        }
    }

    public byte[] asByteArray(int compressionLevel) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DeflaterOutputStream outputStream = new DeflaterOutputStream(byteArrayOutputStream, new Deflater(compressionLevel));
            write(outputStream);
            outputStream.flush();
            outputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException | WolkenException e) {
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
        } catch (IOException | WolkenException e) {
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

    public byte[] checksum() {
        return HashUtil.hash160(asByteArray());
    }

    public abstract int getSerialNumber();

    public static void checkFullyRead(int result, int expected) throws IOException {
        if (result != expected) {
            throw new IOException("expected '" + expected + "' bytes but only received '" + result + "'");
        }
    }

    public static int checkNotEOF(int read) throws IOException {
        if (read < 0) {
            throw new IOException("end of file reached.");
        }

        return read;
    }

    public <T> T fromCompressed(byte[] compressed) throws IOException, WolkenException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed);
        InflaterInputStream inputStream = new InflaterInputStream(byteArrayInputStream);
        read(inputStream);
        inputStream.close();

        return (T) this;
    }
}