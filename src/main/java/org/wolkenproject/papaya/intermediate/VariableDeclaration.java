package org.wolkenproject.papaya.intermediate;

import org.wolkenproject.papaya.runtime.Scope;

public class VariableDeclaration implements Opcode {
    @Override
    public void execute(Scope scope) {
        scope.getStack().push(scope.getNullReference());
    }
}
