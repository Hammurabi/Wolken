package org.wolkenproject.papaya.compiler;

import java.util.List;
import java.util.Set;

public class PapayaFunction extends PapayaMember {
    private final String                name;
    private final Set<PapayaField>      arguments;
    private final List<Token>           statement;
    private byte                        byteCode[];
    private final LineInfo              lineInfo;

    public PapayaFunction(AccessModifier accessModifier, String name, Set<PapayaField> arguments, List<Token> statement, LineInfo lineInfo) {
        super(accessModifier, name);
        this.name = name;
        this.arguments = arguments;
        this.statement = statement;
        this.lineInfo = lineInfo;
        this.byteCode = new byte[0];
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

    public List<Token> getStatement() {
        return statement;
    }

    public byte[] getByteCode() {
        return byteCode;
    }

    public void setByteCode(byte[] byteArray) {
        byteCode = byteArray;
    }

    public void compile(CompilationScope compilationScope) {
    }
}
