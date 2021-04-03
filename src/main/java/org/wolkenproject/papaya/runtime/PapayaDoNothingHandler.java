package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.papaya.compiler.AccessModifier;
import org.wolkenproject.papaya.compiler.PapayaStructure;

import java.util.Stack;

public class PapayaDoNothingHandler extends PapayaHandler {
    public PapayaDoNothingHandler(PapayaObject object) {
        super(object);
    }

    @Override
    public void setMember(byte memberId[], PapayaHandler member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        getPapayaObject().setMember(memberId, member, stackTrace);
    }

    @Override
    public PapayaHandler getMember(byte memberId[], Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        return getPapayaObject().getMember(memberId, stackTrace);
    }

    @Override
    public void call(Scope scope) throws PapayaException {
        getPapayaObject().call(scope);
    }

    @Override
    public PapayaHandler getAtIndex(int index) throws PapayaException {
        return new PapayaDoNothingHandler(getPapayaObject().asContainer().getAtIndex(index, AccessModifier.None));
    }

    @Override
    public void setAtIndex(int index, PapayaHandler handler) {

    }
}
