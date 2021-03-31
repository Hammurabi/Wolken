package org.wolkenproject.papaya.compiler.statements;

import org.wolkenproject.papaya.compiler.PapayaField;
import org.wolkenproject.papaya.compiler.PapayaStatement;

public class FieldDeclarationStatement extends PapayaStatement {
    public FieldDeclarationStatement(PapayaField field, PapayaStatement assignment) {
        super(scope -> scope.newField(field, assignment), field.getLineInfo());
    }
}
