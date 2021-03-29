package org.wolkenproject.core.papaya.compiler.statements;

import org.wolkenproject.core.papaya.compiler.PapayaField;
import org.wolkenproject.core.papaya.compiler.PapayaStatement;

public class FieldDeclarationStatement extends PapayaStatement {
    public FieldDeclarationStatement(PapayaField field, PapayaStatement assignment) {
        super(scope -> scope.newField(field, assignment), field.getLineInfo());
    }
}
