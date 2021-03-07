package org.wolkenproject.core.script;

import java.io.InputStream;
import java.io.OutputStream;

public class MochaObject {
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
        this.children   = new MochaObject[metadata.getChildCount()];
    }

    public Metadata getMetadata() {
        return metadata;
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
