package org.wolkenproject.papaya.compiler;

import java.util.ArrayList;
import java.util.List;

public class PapayaStatement {
    private final static StatementCompiler DefaultCompiler = scope -> {};
    private final List<PapayaStatement> children;
    private final LineInfo              lineInfo;
    private final StatementCompiler     compiler;

    public PapayaStatement(LineInfo lineInfo) {
        this(DefaultCompiler, lineInfo);
    }

    public PapayaStatement(StatementCompiler compiler, LineInfo lineInfo) {
        this.children = new ArrayList<>();
        this.lineInfo = lineInfo;
        this.compiler = compiler;
    }

    public void compile(CompilationScope scope) {
        compiler.compile(scope);
        for (PapayaStatement statement : children) {
            statement.compile(scope);
        }
    }

    public LineInfo getLineInfo() {
        if (lineInfo.getLine() == -1) {
            if (!children.isEmpty()) {
                return children.get(0).getLineInfo();
            }
        }

        return lineInfo;
    }

    public PapayaStatement addChild(PapayaStatement papayaStatement) {
        children.add(papayaStatement);
        return this;
    }
}
