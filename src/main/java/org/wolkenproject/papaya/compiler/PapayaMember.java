package org.wolkenproject.papaya.compiler;

import org.wolkenproject.utils.ByteArray;

import java.math.BigInteger;
import java.util.List;

public class PapayaMember {
    private final AccessModifier        accessModifier;
    private final ByteArray             identifier;
    private final ByteArray             enforcedType;
    private final LineInfo              lineInfo;
    private final List<PapayaStatement> statementList;

//    public PapayaMember(AccessModifier accessModifier, String name, List<PapayaStatement> statementList, LineInfo lineInfo) {
//        this(accessModifier, ByteArray.wrap(name.getBytes(StandardCharsets.UTF_8)), statementList, lineInfo);
//    }

    public PapayaMember(AccessModifier accessModifier, ByteArray identifier, ByteArray enforcedType, List<PapayaStatement> statementList, LineInfo lineInfo) {
        this.accessModifier = accessModifier;
        this.enforcedType = enforcedType;
        this.identifier = identifier;
        this.statementList = statementList;
        this.lineInfo = lineInfo;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    public ByteArray getIdentifier() {
        return identifier;
    }

    public BigInteger getIdentifierInt() {
        return new BigInteger(1, getIdentifier().getArray());
    }

    public List<PapayaStatement> getStatementList() {
        return statementList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\tmember (").append(new String(getIdentifier().getArray())).append(") ").append(" access (").append(getAccessModifier()).append(")\n");
        for (PapayaStatement papayaStatement : statementList) {
            builder.append("\t\t").append(papayaStatement).append("\n");
        }

        return builder.toString();
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }
}
