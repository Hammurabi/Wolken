package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.AccessModifier;
import org.wolkenproject.papaya.compiler.PapayaStructure;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;

import java.util.Stack;

public class ReadOnlyHandler extends PapayaHandler {
    public ReadOnlyHandler(PapayaObject papayaObject) {
        super(papayaObject);
    }

    @Override
    public void setMember(byte[] memberId, PapayaHandler member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        throw new PapayaIllegalAccessException();
    }

    @Override
    public PapayaHandler getMember(byte[] memberId, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        return new ReadOnlyHandler(getPapayaObject().getMember(memberId, stackTrace).getPapayaObject());
    }

    @Override
    public void call(Scope scope) throws PapayaException {
        getPapayaObject().call(scope);
    }

    @Override
    public PapayaHandler getAtIndex(int index) throws PapayaException {
        return new ReadOnlyHandler(getPapayaObject().asContainer().getAtIndex(index));
    }

    @Override
    public void setAtIndex(int index, PapayaHandler handler) throws PapayaException {
        throw new PapayaIllegalAccessException();
    }

    @Override
    public void append(PapayaHandler handler) throws PapayaException {
        throw new PapayaIllegalAccessException();
    }

    @Override
    public AccessModifier getModifier() {
        return AccessModifier.ReadOnly;
    }
}
