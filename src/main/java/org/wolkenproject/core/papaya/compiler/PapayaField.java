package org.wolkenproject.core.papaya.compiler;

public class PapayaField {
    private final String name;
    private final String typeName;
    private final LineInfo lineInfo;
    private final PapayaStatement valueAssignment;

    public PapayaField(String name, String typeName, LineInfo lineInfo, PapayaStatement valueAssignment) {
        this.name = name;
        this.typeName = typeName;
        this.lineInfo = lineInfo;
        this.valueAssignment = valueAssignment;
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }
}
