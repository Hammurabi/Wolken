package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.exceptions.WolkenException;

import java.util.LinkedHashMap;
import java.util.Map;

public class PapayaStructure {
    private final StructureType                 structureType;
    private final Map<String, PapayaField>      fieldMap;
    private final Map<String, PapayaFunction>   functionMap;
    private final LineInfo                      lineInfo;

    public PapayaStructure(StructureType structureType, LineInfo lineInfo) {
        this.structureType  = structureType;
        this.fieldMap       = new LinkedHashMap<>();
        this.functionMap    = new LinkedHashMap<>();
        this.lineInfo       = lineInfo;
    }

    public void addField(String name, PapayaField field) throws WolkenException {
        if (fieldMap.containsKey(name)) {
            throw new WolkenException("redeclaration of field '" + name + "' {"+field+"}.");
        }

        fieldMap.put(name, field);
    }

    public void addFunction(String name, PapayaFunction function) throws WolkenException {
        if (fieldMap.containsKey(name)) {
            throw new WolkenException("redeclaration of function '" + name + "' {"+function+"}.");
        }

        functionMap.put(name, function);
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }
}
