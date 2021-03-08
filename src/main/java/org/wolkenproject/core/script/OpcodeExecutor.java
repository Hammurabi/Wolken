package org.wolkenproject.core.script;

public interface OpcodeExecutor {
    public void execute(OpcodeInputStream stream, VirtualProcess virtualProcess);
}
