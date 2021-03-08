package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.exceptions.UndefClassException;
import org.wolkenproject.utils.HashUtil;

import java.util.HashMap;
import java.util.Map;

public class ClassProvider {
    Map<Integer, MochaClass> classMap;

    public ClassProvider() {
        classMap = new HashMap<>();

        setDefaultMochaClass();
    }

    public void registerClass(MochaClass mochaClass) {
        classMap.put(classMap.size(), mochaClass);
    }

    public MochaClass getClass(int metadata) throws UndefClassException {
        if (!classMap.containsKey(metadata)) {
            throw new UndefClassException("no metadata found for '" + metadata + "'.");
        }

        return classMap.get(metadata);
    }

    public MochaClass getDefaultMochaClass() throws UndefClassException {
        return getClass(0);
    }

    public MochaClass getArrayMochaClass() throws UndefClassException {
        return getClass(1);
    }

    private void setDefaultMochaClass() {
        MochaClass defaultClass = new MochaClass(null);
        defaultClass.setName("Object");
        // (self)
        defaultClass.addFunction("hashCode", (proc)->{ return proc.getClassProvider().getArrayMochaClass().newInstanceNative(proc, proc.getMemoryModule().getStack().pop().getHash()); });
        defaultClass.addFunction("toString", (proc)->{ return null; });
        defaultClass.addFunction("getClassName", (proc)->{ return null; });

        // (self, other)
        defaultClass.addFunction("add", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("sub", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("mul", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("div", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("mod", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("shiftL", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("shiftR", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("arithmeticShift", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("and", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("or", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("not", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("xor", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
        defaultClass.addFunction("pow", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });

        // (self)
        defaultClass.addFunction("negate", (proc)->{ return proc.getMemoryModule().getStack().pop(); });

        registerClass(defaultClass);

        MochaClass arrayClass = new MochaClass(defaultClass);
        arrayClass.setName("Array");
        arrayClass.addFunction("append", (proc)->{ MochaObject append = proc.getMemoryModule().getStack().pop(); MochaObject self = proc.getMemoryModule().getStack().pop(); if (!append.equals(self)) { append.append(append); } throw new MochaException("cannot append self to array."); });
        arrayClass.addFunction("pop", (proc)->{ return null; });
        arrayClass.addFunction("len", (proc)->{ return null; });
        arrayClass.addFunction("reshape", (proc)->{ return null; });
        arrayClass.addFunction("shape", (proc)->{ return null; });
        registerClass(arrayClass);
    }
}
