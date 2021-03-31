package org.wolkenproject.papaya.compiler.statements;

import org.wolkenproject.papaya.compiler.LineInfo;
import org.wolkenproject.papaya.compiler.PapayaStatement;

public class FunctionCallStatement extends PapayaStatement {
    public FunctionCallStatement(String tokenValue, PapayaStatement arguments, LineInfo lineInfo) {
        super(scope -> scope.memberCall(tokenValue, arguments), lineInfo);
    }
}
