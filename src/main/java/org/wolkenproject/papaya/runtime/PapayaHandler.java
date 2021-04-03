package org.wolkenproject.papaya.runtime;

import org.wolkenproject.papaya.compiler.PapayaStructure;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.exceptions.WolkenException;

import java.util.Stack;

public abstract class PapayaHandler {
    private final PapayaObject papayaObject;

    public PapayaHandler(PapayaObject papayaObject) {
        this.papayaObject = papayaObject;
    }

    public abstract void setMember(int memberId, PapayaHandler member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException;
    public abstract PapayaHandler getMember(int memberId, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException;
    public abstract void call(Scope scope) throws WolkenException;

    public PapayaObject getPapayaObject() {
        return papayaObject;
    }

    public static final PapayaHandler doNothingHandler(PapayaObject object) {
        return new PapayaDoNothingHandler(object);
    }

    public static final PapayaHandler readOnlyHandler(PapayaObject object) {
        return new PapayaReadOnlyWrapper(object);
    }
}