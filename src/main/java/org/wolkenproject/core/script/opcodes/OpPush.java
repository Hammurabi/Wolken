package org.wolkenproject.core.script.opcodes;

import org.wolkenproject.core.script.Opcode;
import org.wolkenproject.core.script.VirtualProcess;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;

public class OpPush extends Opcode {
    private byte array[];

    public OpPush() {
        this(new byte[0]);
    }

    public OpPush(byte array[]) {
        super("push", "push 'x' into the stack", "push [arg] [value]");
        this.array = array;
    }

    @Override
    public void execute(VirtualProcess virtualProcess) {
        virtualProcess.getMemoryModule().getStack().push(new )
    }

    @Override
    public void write(BitOutputStream outputStream) {
    }

    @Override
    public void read(BitInputStream inputStream) {
    }

    @Override
    public Opcode makeCopy() {
        return new OpPush();
    }
}
