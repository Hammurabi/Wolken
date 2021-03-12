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

public class OpIConst_4bits extends Opcode {
    public OpIConst_4bits(String name, String desc, String usage) {
        super("OpIConst{d}", "push an int to the stack (0-15)", "iconst{d}");
    }

    @Override
    public void execute(Scope scope) throws MochaException {
    }

    @Override
    public void write(BitOutputStream outputStream) throws IOException {
    }

    @Override
    public void read(BitInputStream inputStream) throws IOException {
    }

    @Override
    public Opcode makeCopy() {
        return null;
    }
}
