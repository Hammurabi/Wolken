package org.wolkenproject.core.script.opcodes;

import org.wolkenproject.core.script.Opcode;
import org.wolkenproject.core.script.VirtualProcess;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;

public class OpHalt extends Opcode {
    public OpHalt() {
        super("halt", "stop the process (and sub-processes).", "halt");
    }

    @Override
    public void execute(VirtualProcess virtualProcess) {
        virtualProcess.stopProcess(0);
    }

    @Override
    public void write(BitOutputStream outputStream) {
    }

    @Override
    public void read(BitInputStream inputStream) {
    }

    @Override
    public Opcode makeCopy() {
        return new OpHalt();
    }
}
