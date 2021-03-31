package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.papaya.compiler.PapayaStructure;

import java.util.Stack;

public class PapayaDoNothingHandler extends PapayaHandler {
    public PapayaDoNothingHandler(PapayaObject object) {
        super(object);
    }

    @Override
    public void setMember(int memberId, PapayaHandler member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        getPapayaObject().setMember(memberId, member, stackTrace);
    }

    @Override
    public PapayaHandler getMember(int memberId, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        return getPapayaObject().getMember(memberId, stackTrace);
    }

    @Override
    public void call(Scope scope) throws WolkenException {
        getPapayaObject().call(scope);
    }
}
