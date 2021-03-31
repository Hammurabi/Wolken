package org.wolkenproject.papaya.compiler.statements;

import org.wolkenproject.papaya.compiler.LineInfo;
import org.wolkenproject.papaya.compiler.PapayaStatement;
import org.wolkenproject.papaya.compiler.Token;

public class VariableAssignment extends PapayaStatement {
    public VariableAssignment(Token name, PapayaStatement assignment, LineInfo lineInfo) {
        super(scope->{
            assignment.compile(scope);
            scope.assignValue(name);
        }, lineInfo);
    }
}
