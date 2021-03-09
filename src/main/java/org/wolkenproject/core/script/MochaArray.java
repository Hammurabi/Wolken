package org.wolkenproject.core.script;

import org.wolkenproject.core.script.MochaClass;
import org.wolkenproject.core.script.MochaObject;
import org.wolkenproject.core.script.VirtualProcess;
import org.wolkenproject.exceptions.UndefClassException;

public class MochaArray extends MochaObject {
    private MochaObject array[];

    public MochaArray(VirtualProcess virtualProcess) throws UndefClassException {
        super(virtualProcess);
    }

    public MochaArray(VirtualProcess virtualProcess, int metadata) throws UndefClassException {
        super(virtualProcess, metadata);
    }

    public MochaArray(VirtualProcess virtualProcess, MochaClass mochaClass) {
        super(virtualProcess, mochaClass);
    }

    public void setArrayLength(int length) {
        this.array = new MochaObject[length];
    }
}
