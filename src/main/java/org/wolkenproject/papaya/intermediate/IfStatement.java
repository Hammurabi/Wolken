package org.wolkenproject.papaya.intermediate;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.runtime.Scope;

import java.util.List;

public class IfStatement implements Opcode {
    private final Opcode        condition;
    private final List<Opcode>  opcodes;

    public IfStatement(Opcode condition, List<Opcode> opcodes) {
        this.condition  = condition;
        this.opcodes    = opcodes;
    }

    @Override
    public void execute(Scope scope) throws PapayaException {
        condition.execute(scope);
        if (scope.getStack().pop().asBool()) {
            for (Opcode opcode : opcodes) {
                opcode.execute(scope);
            }
        }
    }
}
