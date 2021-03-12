package org.wolkenproject.core.script.opcodes;

import org.wolkenproject.core.script.Contract;
import org.wolkenproject.core.script.Opcode;
import org.wolkenproject.core.script.Scope;
import org.wolkenproject.core.script.internal.MochaNumber;
import org.wolkenproject.core.script.internal.MochaObject;
import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Stack;

public class OpIConst extends Opcode {
    private byte        integer[];
    private boolean     signed;

    public OpIConst() {
        this(new byte[] { 0 });
    }

    public OpIConst(byte integer[]) {
        super("iconst", "push (u)int8(8-64) into the stack", "push [arg] [value]");
        this.integer = integer;
    }

    @Override
    public void execute(Scope scope) throws MochaException {
        scope.getStack().push(new MochaNumber(integer, signed));
    }

    @Override
    public void write(BitOutputStream outputStream) throws IOException {
        // 3 bits to define the type ( 4bit, 8bit, 12bit, , 16bit, 32bit, 64bit )
        int type = 0;
        BigInteger integer = new BigInteger(this.integer);
        int bitLength = integer.bitLength();

        outputStream.write(signed ? 1 : 0);

        if (bitLength <= 4) {
            type = 0;
        }
        else if (bitLength <= 8) {
            type = 1;
        }
        else if (bitLength <= 12) {
            type = 2;
        }
        else if (bitLength <= 16) {
            type = 3;
        }
        else if (bitLength <= 32) {
            type = 4;
        }
        else if (bitLength <= 64) {
            type = 5;
        }
        else if (bitLength <= 128) {
            type = 6;
        } else if (bitLength <= 256) {
            type = 7;
        }

        outputStream.write(Utils.getBit(type, 0));
        outputStream.write(Utils.getBit(type, 1));

        for (int i = 0; i < bitLength; i ++) {
            outputStream.write(integer.testBit(i) ? 1 : 0);
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
