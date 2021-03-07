package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.UndefClassException;
import org.wolkenproject.exceptions.UndefFunctionException;

import java.io.InputStream;
import java.io.OutputStream;

public class MochaObject {
    public static final char        DefaultMetadata = 0;

    private Metadata                metadata;
    private MochaFunction           functions[];
    private MochaObject             members[];

    public MochaObject(VirtualMachine virtualMachine) throws UndefClassException {
        this(virtualMachine, DefaultMetadata);
    }

    public MochaObject(VirtualMachine virtualMachine, char metadata) throws UndefClassException {
        this(virtualMachine, virtualMachine.getMetadataProvider().getMetadata(metadata));
    }

    public MochaObject(VirtualMachine virtualMachine, Metadata metadata) {
        this.metadata   = new Metadata();
        this.functions  = new MochaFunction[metadata.getFunctionCount()];
        this.members    = new MochaObject[metadata.getMemberCount()];
    }

    public MochaFunction getFunction(int functionId) throws UndefFunctionException {
        if (functionId >= functions.length) {
            throw new UndefFunctionException("no function '" + functionId + "' found.");
        }

        return functions[functionId];
    }

    public MochaObject getMember(int memberId) throws UndefClassException {
        if (memberId >= members.length) {
            throw new UndefClassException("no member '" + memberId + "' found.");
        }

        return members[memberId];
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
