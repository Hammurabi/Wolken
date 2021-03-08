package org.wolkenproject.core.script.opcodes;

import org.wolkenproject.core.script.Opcode;
import org.wolkenproject.core.script.VirtualProcess;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;

public class OpHalt extends Opcode {
    public OpHalt(String name, String desc, String usage) {
        super(name, desc, usage);
    }

    @Override
    public void execute(VirtualProcess virtualProcess) {

    }

    @Override
    public void write(BitOutputStream outputStream) {

    }

    @Override
    public void read(BitInputStream inputStream) {

    }

    @Override
    public Opcode makeCopy() {
        return null;
    }
}
