package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.InvalidMemoryAccess;

public class MemoryModule {
    private MemoryState memoryState;
    private byte        memory[];
    private Register    registers[];

    public MemoryModule() {
        this.registers = new Register[16];
    }

    public Register getRegister(int registerID) {
        return registers[registerID];
    }

    public void setState(MemoryState memoryState) {
        this.memoryState = memoryState;
    }

    public MemoryState getState() {
        return memoryState;
    }

    private byte[] getBytesAt(int offset, int length) throws InvalidMemoryAccess {
        if (offset + length >= memory.length) {
            throw new InvalidMemoryAccess("requested memory range (" + offset + ", " + length + ") goes out of bounds.");
        }

        byte bytes[] = new byte[length];

        for (int i = 0; i < memory.length; i ++) {
            bytes[i] = memory[i + offset];
        }

        return bytes;
    }

    private void memcpy(int dst, int src, int len) throws InvalidMemoryAccess {
        if ((dst + len) >= memory.length || (src + len) >= memory.length) {
            throw new InvalidMemoryAccess("memcpy of memory range (" + dst + ", " + src + ", " + len + ") goes out of bounds.");
        }

        for (int i = 0; i < len; i ++) {
            memory[dst + i] = memory[src + i];
        }
    }

    protected static class Register {
        protected static final int  RegisterLength = 32;
        private MemoryModule        memoryModule;
        private int                 pointer;

        public byte[] getBytes() throws InvalidMemoryAccess {
            return memoryModule.getBytesAt(pointer, RegisterLength);
        }

        public void set(Register other) throws InvalidMemoryAccess {
            memoryModule.memcpy(pointer, other.pointer, RegisterLength);
        }

        public void reset() {
            memoryModule.clear(pointer, RegisterLength);
        }
    }
}
