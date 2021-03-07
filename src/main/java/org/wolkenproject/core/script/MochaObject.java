package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.UndefClassException;

import java.io.InputStream;
import java.io.OutputStream;

public class MochaObject {
    public static final char        DefaultMetadata = 0;

    private Metadata                metadata;
    private MochaFunction           functions[];
    private MochaObject             children[];

    public MochaObject(VirtualMachine virtualMachine) throws UndefClassException {
        this(virtualMachine, DefaultMetadata);
    }

    public MochaObject(VirtualMachine virtualMachine, char metadata) throws UndefClassException {
        this(virtualMachine, virtualMachine.getMetadataProvider().getMetadata(metadata));
    }

    public MochaObject(VirtualMachine virtualMachine, Metadata metadata) {
        this.metadata   = new Metadata();
        this.functions  = new MochaFunction[metadata.getFunctionCount()];
        this.children   = new MochaObject[metadata.getChildCount()];
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
