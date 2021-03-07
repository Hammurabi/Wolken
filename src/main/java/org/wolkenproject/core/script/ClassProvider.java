package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.UndefClassException;

import java.util.HashMap;
import java.util.Map;

public class ClassProvider {
    Map<Integer, MochaClass> classMap;

    public ClassProvider() {
        classMap = new HashMap<>();
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
}
