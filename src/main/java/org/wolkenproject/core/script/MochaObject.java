package org.wolkenproject.core.script;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MochaObject extends SerializableI {
    private Metadata metadata;

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        metadata.write(stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new MochaObject();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(MochaObject.class);
    }

    public static class Metadata implements SerializableI {
        @Override
        public void write(OutputStream stream) throws IOException, WolkenException {
        }

        @Override
        public void read(InputStream stream) throws IOException, WolkenException {
        }

        @Override
        public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
            return null;
        }

        @Override
        public int getSerialNumber() {
            return 0;
        }
    }
}
