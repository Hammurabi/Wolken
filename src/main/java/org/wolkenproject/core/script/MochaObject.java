package org.wolkenproject.core.script;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MochaObject extends SerializableI {
    public static final Metadata DefaultMetadata = new Metadata();
    private Metadata                metadata;
    private MochaFunction           functions[];
    private MochaObject             children[];

    public MochaObject() {
        this(new Metadata(DefaultMetadata));
    }

    public MochaObject(Metadata metadata) {
        this.metadata   = new Metadata();
        this.functions  = new MochaFunction[metadata.getFunctionCount()];
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public MochaObject default__hashCode() {
        return
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        metadata.write(stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        metadata.read(stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new MochaObject();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(MochaObject.class);
    }

    public static class Metadata {
        private int parentClass;

        public Metadata() {
        }

        public Metadata(Metadata metadata) {
        }

        public void write(OutputStream outputStream) {}

        public void read(InputStream inputStream)  {}
    }
}
