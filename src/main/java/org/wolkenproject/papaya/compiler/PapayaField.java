package org.wolkenproject.papaya.compiler;

import org.wolkenproject.papaya.AccessModifier;

public class PapayaField extends PapayaMember {
    private final String name;
    private final String typeName;
    private final LineInfo lineInfo;
    private final PapayaStatement valueAssignment;

    public PapayaField(AccessModifier accessModifier, String name, String typeName, LineInfo lineInfo, PapayaStatement valueAssignment) {
        super(accessModifier, name);
        this.name = name;
        this.typeName = typeName;
        this.lineInfo = lineInfo;
        this.valueAssignment = valueAssignment;
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getType() {
        return typeName;
    }

    public PapayaStatement getValueAssignment() {
        return valueAssignment;
    }
}
