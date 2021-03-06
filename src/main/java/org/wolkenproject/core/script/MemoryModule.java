package org.wolkenproject.core.script;

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

    private byte[] getBytesAt(int offset, int length) {
        byte bytes[] = new byte[length];
        for (int i = offset; i < memory.le)
            return ;
    }

    protected static class Register {
        protected static final int  RegisterLength = 32;
        private MemoryModule        memoryModule;
        private int                 pointer;

        public byte[] getBytes() {
            return memoryModule.getBytesAt(pointer, RegisterLength);
        }

        public void set(Register other) {
            memoryModule.memcpy(pointer, other.pointer, RegisterLength);
        }

        public void reset() {
            memoryModule.clear(pointer, RegisterLength);
        }
    }
}
