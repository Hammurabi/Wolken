package org.wolkenproject.core.script.opcodes;

import org.wolkenproject.core.script.Opcode;
import org.wolkenproject.core.script.VirtualProcess;
import org.wolkenproject.core.script.objects.MochaArray;
import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;
import org.wolkenproject.utils.Utils;

import java.io.IOException;

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
    public void execute(VirtualProcess virtualProcess) throws MochaException {
        MochaArray array = new MochaArray(virtualProcess, virtualProcess.getClassProvider().getArrayMochaClass());
        array.setArrayLength(this.array.length);
        for (int i = 0; i < this.array.length; i ++) {
            array.setMember(i, null);
        }

        virtualProcess.getMemoryModule().getStack().push(array);
    }

    @Override
    public void write(BitOutputStream outputStream) throws IOException {
        // 2 bits to define the type ( 5bit, 7bit, 10bit, 12bit )
        int type = 0;
        int bitLength = 0;

        if (array.length <= 32) {
            type = 0;
            bitLength = 5;
        }
        else if (array.length <= 128) {
            type = 1;
            bitLength = 7;
        }
        else if (array.length <= 1024) {
            type = 2;
            bitLength = 10;
        } else if (array.length <= 4096) {
            type = 3;
            bitLength = 12;
        }

        outputStream.write(Utils.getBit(type, 0));
        outputStream.write(Utils.getBit(type, 1));

        int arrayLength = array.length;
        for (int i = 0; i < bitLength; i ++) {
            outputStream.write(Utils.getBit(arrayLength, i));
        }

        for (byte b : array) {
            for (int i = 0; i < 8; i ++) {
                outputStream.write(Utils.getBit(b, i));
            }
        }
    }

    @Override
    public void read(BitInputStream inputStream) throws IOException {
        int type        = 0;
        int bitLength   = 0;

        type            = Utils.setBit(type, 0, inputStream.read());
        type            = Utils.setBit(type, 1, inputStream.read());

        switch (type) {
            case 0:
                bitLength   = 4;
                break;
            case 1:
                bitLength   = 7;
                break;
            case 2:
                bitLength   = 10;
                break;
            case 3:
                bitLength   = 12;
                break;
        }

        int arrayLength = 0;
        for (int i = 0; i < bitLength; i ++) {
            arrayLength     = Utils.setBit(arrayLength, i, inputStream.read());
        }

        array           = new byte[arrayLength];

        for (int i = 0; i < arrayLength; i ++) {
            int b = 0;

            for (int bit = 0; bit < 8; bit ++) {
                b = Utils.setBit(b, bit, inputStream.read());
            }

            array[i]    = (byte) b;
        }
    }

    @Override
    public Opcode makeCopy() {
        return new OpPush(array);
    }
}
