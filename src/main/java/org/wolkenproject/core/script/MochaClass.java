package org.wolkenproject.core.script;

import org.wolkenproject.encoders.Base16;
import org.wolkenproject.utils.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MochaClass {
    private Map<String, Tuple<Integer, MochaFunction>> functions;

    public MochaClass() {
        functions = new HashMap<>();
        addFunction("hashCode", (mem)->{ return new MochaByteArray(mem.popStack().checksum()); });
        addFunction("toString", (mem)->{ return new MochaString(Base16.encode(mem.popStack().checksum())); });
    }

    public void addFunction(String functionName, MochaFunction function) {
        functions.put(functionName, new Tuple<>(functions.size(), function));
    }

    // call any functions defined in this class
    public MochaObject call(int functionPtr, MemoryModule memoryModule) {
        return null;
    }
}
