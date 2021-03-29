package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.exceptions.WolkenException;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationBinaryInterface {
    // this contains structures in order of declaration
    private final Map<String, PapayaStructure> structureMap;

    public ApplicationBinaryInterface() {
        this.structureMap = new LinkedHashMap<>();
    }

    public void addStructure(String name, PapayaStructure structure) throws WolkenException {
        if (structureMap.containsKey(name)) {
            throw new WolkenException("redeclaration of structure '" + name + "' {"+structure.getLineInfo()+"}.");
        }

        structureMap.put(name, structure);
    }
}
