package org.wolkenproject.papaya.compiler;

import org.wolkenproject.utils.ByteArray;

import java.util.List;
import java.util.Set;

public class PapayaFunction extends PapayaMember {
    private final Set<PapayaField>      arguments;
    private final List<Token>           statement;
    private byte                        byteCode[];
    private final LineInfo              lineInfo;

    public PapayaFunction(AccessModifier accessModifier, String name, Set<PapayaField> arguments, List<Token> statement, LineInfo lineInfo) {
        this(accessModifier, ByteArray.wrap(name), arguments, statement, lineInfo);
    }

    public PapayaFunction(AccessModifier accessModifier, ByteArray name, Set<PapayaField> arguments, List<Token> statement, LineInfo lineInfo) {
        super(accessModifier, name, null, null, lineInfo);
        this.arguments = arguments;
        this.statement = statement;
        this.lineInfo = lineInfo;
        this.byteCode = new byte[0];
    }

    public LineInfo getLineInfo() {
        return lineInfo;
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
