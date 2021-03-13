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

    public int nextByte() throws EmptyProgramCounterException {
        if (hasNext()) {
            return Byte.toUnsignedInt(program.get());
        }

        throw new EmptyProgramCounterException();
    }

    public int nextShort() throws EmptyProgramCounterException {
        if (remaining() >= 2) {
            return program.getShort();
        }

        throw new EmptyProgramCounterException();
    }

    public int nextUnsignedShort() throws EmptyProgramCounterException {
        if (remaining() >= 2) {
            return program.getChar();
        }

        throw new EmptyProgramCounterException();
    }

    public int nextInt24() throws EmptyProgramCounterException {
        if (remaining() >= 3) {
            return Utils.makeInt((byte) 0, program.get(), program.get(), program.get());
        }

        throw new EmptyProgramCounterException();
    }

    public int nextInt() throws EmptyProgramCounterException {
        if (remaining() >= 4) {
            return program.getInt();
        }

        throw new EmptyProgramCounterException();
    }

    public long nextLong() throws EmptyProgramCounterException {
        if (remaining() >= 8) {
            return program.getLong();
        }

        throw new EmptyProgramCounterException();
    }

    public byte[] next(int length) throws EmptyProgramCounterException {
        if (remaining() >= length) {
            byte array[] = new byte[length];
            program.get(array);

            return array;
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
