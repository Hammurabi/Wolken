package org.wolkenproject.papaya.intermediate;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.Struct;
import org.wolkenproject.papaya.runtime.Scope;

public class Divide implements Opcode {
    private final Opcode a;
    private final Opcode b;

    public Divide(Opcode a, Opcode b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void execute(Scope scope) throws PapayaException {
        b.execute(scope);
        a.execute(scope);
        scope.callOperator(Struct.Operator.Div);
    }
}
