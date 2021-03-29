package org.wolkenproject.core.papaya.compiler;

import java.util.Set;

public class PapayaFunction {
    private final String                name;
    private final Set<PapayaField>      arguments;
    private final Set<PapayaStatement>  statementSet;
    private final LineInfo              lineInfo;

    public PapayaFunction(String name, Set<PapayaField> arguments, Set<PapayaStatement> statementSet, LineInfo lineInfo) {
        this.name = name;
        this.arguments = arguments;
        this.statementSet = statementSet;
        this.lineInfo = lineInfo;
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public String getName() {
        return name;
    }

    public Set<PapayaField> getArguments() {
        return arguments;
    }

    public Set<PapayaStatement> getStatementSet() {
        return statementSet;
    }
}
