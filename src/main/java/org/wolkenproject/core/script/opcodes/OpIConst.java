package org.wolkenproject.core.script.opcodes;

import org.wolkenproject.core.script.Opcode;
import org.wolkenproject.core.script.VirtualProcess;
import org.wolkenproject.core.script.MochaArray;
import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;
import org.wolkenproject.utils.Utils;

import java.io.IOException;

public class OpIConst extends Opcode {
    private long integer;

    public OpIConst() {
        this(0);
    }

    public OpIConst(long integer) {
        super("iconst", "push (u)int8(8-64) into the stack", "push [arg] [value]");
        this.integer = integer;
    }

    @Override
    public void execute(VirtualProcess virtualProcess) throws MochaException {
        MochaArray array = new MochaArray(virtualProcess, virtualProcess.getClassProvider().getArrayMochaClass());
        virtualProcess.getMemoryModule().getStack().push(array);
    }

    private static boolean isSigned(long integer) {
        return integer < 0;
    }

    @Override
    public void write(BitOutputStream outputStream) throws IOException {
        // 2 bits to define the type ( 8bit, 16bit, 32bit, 64bit )
        boolean isSigned = isSigned(integer);
        int type = 0;
        int bitLength = 0;

        outputStream.write(isSigned ? 1 : 0);

        if (integer >= 0 && integer <= 255) {
            type = 0;
            bitLength = 8;
        }
        else if (integer >= 0 && integer <= 65535) {
            type = 1;
            bitLength = 16;
        }
        else if (integer >= 0 && integer <= 4294967295L) {
            type = 2;
            bitLength = 32;
        } else if (integer >= 0 && integer <= 4096) {
            type = 3;
            bitLength = 64;
        }

        outputStream.write(Utils.getBit(type, 0));
        outputStream.write(Utils.getBit(type, 1));

        for (int i = 0; i < bitLength; i ++) {
            outputStream.write(Utils.getBit(integer, i));
        }
    }

    @Override
    public void read(BitInputStream inputStream) throws IOException {
    }

    @Override
    public Opcode makeCopy() {
        return new OpIConst(integer);
    }
}
