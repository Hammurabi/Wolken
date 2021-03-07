package org.wolkenproject.core.script;

import java.io.InputStream;
import java.io.OutputStream;

public class MochaObject {
    public static final char        DefaultMetadata = 0;
    private Metadata                metadata;
    private MochaFunction           functions[];
    private MochaObject             children[];

    public MochaObject(VirtualMachine virtualMachine) {
        this(virtualMachine, DefaultMetadata);
    }

    public MochaObject(VirtualMachine virtualMachine, char metadata) {
        this(virtualMachine, virtualMachine.getMetadataProvider().get(metadata));
    }

    public MochaObject(VirtualMachine virtualMachine, Metadata metadata) {
        this.metadata   = new Metadata();
        this.functions  = new MochaFunction[virtualMachine.getClassMetadata()];
        this.children   = new MochaObject[metadata.getChildCount()];
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
