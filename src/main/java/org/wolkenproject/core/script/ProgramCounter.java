package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.EmptyProgramCounterException;
import org.wolkenproject.exceptions.UndefOpcodeException;
import org.wolkenproject.utils.Utils;

import java.nio.ByteBuffer;

public class ProgramCounter {
    private ByteBuffer      program;
    private OpcodeRegister  register;

    public ProgramCounter(ByteBuffer program, OpcodeRegister register) {
        this.program = program;
        this.register= register;
        program.position(0);
    }

    public int nextInt() {
        if (remaining() >= 4) {
            return Utils.makeInt(program.get(), program.get(), program.get(), program.get());
        }

        throw new EmptyProgramCounterException();
    }

    public int remaining() {
        return program.remaining();
    }

    public Opcode next() throws UndefOpcodeException, EmptyProgramCounterException {
        if (program.hasRemaining()) {
            int nextOp = program.get();
            return register.getOpcode(nextOp);
        }

        throw new EmptyProgramCounterException();
    }

    public boolean hasNext() {
        return program.hasRemaining();
    }
}
