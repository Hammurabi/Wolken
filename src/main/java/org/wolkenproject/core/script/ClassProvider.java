package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.UndefClassException;

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
        defaultClass.addFunction("hashCode", new MochaFunction() {
            @Override
            public MochaObject execute(MemoryModule memoryModule) {
                return null;
            }
        });
        defaultClass.addFunction("toString", new MochaFunction() {
            @Override
            public MochaObject execute(MemoryModule memoryModule) {
                return null;
            }
        });
        defaultClass.addFunction("getClassName", new MochaFunction() {
            @Override
            public MochaObject execute(MemoryModule memoryModule) {
                return null;
            }
        });

        registerClass(defaultClass);

        registerClass(defaultClass);
    }
}
