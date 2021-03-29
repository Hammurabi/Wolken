package org.wolkenproject.core.papaya.compiler;

import java.util.LinkedHashMap;
import java.util.Map;

public class PapayaStructure {
    private final StructureType             structureType;
    private final Map<String, PapayaField>  fieldMap;

    public PapayaStructure(StructureType structureType) {
        this.structureType  = structureType;
        this.fieldMap       = new LinkedHashMap<>();
    }
}
