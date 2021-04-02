package org.wolkenproject.papaya.compiler;

public class PapayaField extends PapayaMember {
    private final String name;
    private final String typeName;
    private final LineInfo lineInfo;
    private final Token valueAssignment;

    public PapayaField(AccessModifier accessModifier, String name, String typeName, LineInfo lineInfo, Token valueAssignment) {
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

    public Token getValueAssignment() {
        return valueAssignment;
    }
}
