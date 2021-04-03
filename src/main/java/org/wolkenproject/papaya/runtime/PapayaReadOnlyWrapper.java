package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.AccessModifier;
import org.wolkenproject.papaya.compiler.PapayaStructure;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.exceptions.WolkenException;

import java.util.Stack;

public class PapayaReadOnlyWrapper extends PapayaHandler {
    public PapayaReadOnlyWrapper(PapayaObject papayaObject) {
        super(papayaObject);
    }

    @Override
    public void setMember(byte[] memberId, PapayaHandler member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {

    }

    @Override
    public PapayaHandler getMember(byte[] memberId, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        return null;
    }

    @Override
    public void call(Scope scope) throws PapayaException {
        getPapayaObject().call(scope);
    }

    @Override
    public PapayaHandler getAtIndex(int index) throws PapayaException {
        return null;
    }

    @Override
    public void setAtIndex(int index, PapayaHandler handler) throws PapayaException {
    }

    @Override
    public AccessModifier getModifier() {
        return AccessModifier.ReadOnly;
    }
}
