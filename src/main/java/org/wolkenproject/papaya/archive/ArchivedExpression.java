package org.wolkenproject.papaya.archive;

import org.wolkenproject.papaya.compiler.Compiler;
import org.wolkenproject.papaya.compiler.Expression;
import org.wolkenproject.papaya.compiler.LineInfo;

public class ArchivedExpression {
    private final LineInfo lineInfo;

    public ArchivedExpression(LineInfo lineInfo) {
        this.lineInfo = lineInfo;
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public Expression compile(ArchivedStruct parent, Compiler compiler) {
        return null;
    }
}
