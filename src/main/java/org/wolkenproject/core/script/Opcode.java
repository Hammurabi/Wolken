package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;

import java.io.IOException;
import java.util.Stack;

public abstract class Opcode {
    private String          name;
    private int             identifier;
    private String          desc;
    private String          usage;

    public Opcode(String name, String desc, String usage) {
        this.name = name;
        this.desc = desc;
        this.usage= usage;
    }

    public abstract void execute(Contract contract, Stack<MochaObject> stack) throws MochaException;
    public abstract void write(BitOutputStream outputStream) throws IOException;
    public abstract void read(BitInputStream inputStream) throws IOException;
    public abstract Opcode makeCopy();

    protected void setIdentifier(int id) {
        this.identifier = id;
    }

    public String getName() {
        return name;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getDesc() {
        return desc;
    }

    public String getUsage() {
        return usage;
    }
}
