package org.wolkenproject.core.papaya.compiler.statements;

import org.wolkenproject.core.papaya.compiler.LineInfo;
import org.wolkenproject.core.papaya.compiler.PapayaStatement;

import java.util.Set;

public class FunctionCallStatement extends PapayaStatement {
    public FunctionCallStatement(String tokenValue, PapayaStatement arguments, LineInfo lineInfo) {
        super(scope -> scope.memberCall(tokenValue, arguments), lineInfo);
    }
}
