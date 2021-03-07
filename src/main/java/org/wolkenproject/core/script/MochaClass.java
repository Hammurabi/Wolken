package org.wolkenproject.core.script;

import java.util.ArrayList;
import java.util.List;

public class MochaClass {
    private List<MochaFunction> functions;

    public MochaClass() {
        functions = new ArrayList<>();
        addFunction("hashCode", (mem)->{ return null; });
    }

    public void addFunction(String functionName, MochaFunction function) {
        functions.add(function);
    }

    // call any functions defined in this class
    public MochaObject call(int functionPtr, MemoryModule memoryModule) {
        return null;
    }
}
