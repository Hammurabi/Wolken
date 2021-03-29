package org.wolkenproject.core.papaya.compiler;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationBinaryInterface {
    // this contains structures in order of declaration
    private final Map<String, PapayaStructure> structureMap;

    public ApplicationBinaryInterface() {
        this.structureMap = new LinkedHashMap<>();
    }
}
