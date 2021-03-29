package org.wolkenproject.core.papaya.compiler;

public class PapayaStatement {
    private final PapayaStatement left;
    private final PapayaStatement right;

    public PapayaStatement(PapayaStatement left, PapayaStatement right) {
        this.left   = left;
        this.right  = right;
    }

    public void compile(CompilationScope scope) {
        left.compile(scope);
        right.compile(scope);
    }

    public PapayaStatement getLeft() {
        return left;
    }

    public PapayaStatement getRight() {
        return right;
    }
}
