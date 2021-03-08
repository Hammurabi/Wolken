package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.InvalidOpcodeException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.BitInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OpcodeList {
    // this class should be used to store opcodes
    // it will compact them in the most optimal
    // way for storage/network transfers to help
    // reduce the cost of transactions.

    private Opcode  opcodes[];

    public OpcodeList() {
    }

    public void read(OpcodeRegister register, byte opcodes[]) throws InvalidOpcodeException {
        BitInputStream inputStream = new BitInputStream(opcodes);
        int counter = 0;
        int argCount= 0;


        while ((counter + argCount) < opcodes.length) {
            int opcodeValue = Byte.toUnsignedInt(opcodes[counter ++]);
            Opcode opcode = register.getOpcode(opcodeValue);
            if (opcode == null) {
                throw new InvalidOpcodeException("invalid opcode '" + opcodeValue + "'.");
            }

            argCount
        }
    }
}
