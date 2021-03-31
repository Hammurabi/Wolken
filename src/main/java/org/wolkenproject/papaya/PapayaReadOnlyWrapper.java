package org.wolkenproject.papaya;

import org.wolkenproject.papaya.compiler.PapayaStructure;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.exceptions.WolkenException;

import java.util.Stack;

public class PapayaReadOnlyWrapper extends PapayaHandler {
    public PapayaReadOnlyWrapper(PapayaObject papayaObject) {
        super(papayaObject);
    }

    @Override
    public void setMember(int memberId, PapayaHandler member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        throw new PapayaIllegalAccessException();
    }

    @Override
    public PapayaHandler getMember(int memberId, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        return new PapayaReadOnlyWrapper(getPapayaObject().getMember(memberId, stackTrace).getPapayaObject());
    }

    @Override
    public void call(Scope scope) throws WolkenException {
        throw new WolkenException("this object is not callable.");
    }
}
