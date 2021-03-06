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

    private byte[] getBytesAt(int offset, int len) throws InvalidMemoryAccess {
        checkRanges(offset, len, "requested memory range (" + offset + ", " + len + ") goes out of bounds.");
        byte bytes[] = new byte[len];

        for (int i = 0; i < len; i ++) {
            bytes[i] = memory[i + offset];
        }

        return bytes;
    }

    private void memcpy(int dst, int src, int len) throws InvalidMemoryAccess {
        checkRanges(src, len, "memcpy of memory range (" + dst + ", " + src + ", " + len + ") goes out of bounds.");
        checkRanges(dst, len, "memcpy of memory range (" + dst + ", " + src + ", " + len + ") goes out of bounds.");

        for (int i = 0; i < len; i ++) {
            memory[dst + i] = memory[src + i];
        }
    }

    private void clear(int offset, int len) throws InvalidMemoryAccess {
        fill(offset, len, 0);
    }

    private void fill(int offset, int len, int value) throws InvalidMemoryAccess {
        checkRanges(offset, len, "fill range ('" + offset + "', '" + len + "') goes out of bounds.");

        for (int i = 0; i < len; i ++) {
            memory[offset + i] = (byte) value;
        }
    }

    private void checkRanges(int offset, int len, String msg) throws InvalidMemoryAccess {
        if (offset < 0) {
            throw new InvalidMemoryAccess(msg);
        }

        if (offset + len >= memory.length || offset + len < 0) {
            throw new InvalidMemoryAccess(msg);
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

        public void reset() throws InvalidMemoryAccess {
            memoryModule.clear(pointer, RegisterLength);
        }
    }
}
