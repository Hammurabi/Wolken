package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.AccessModifier;
import org.wolkenproject.papaya.compiler.PapayaStructure;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;

import java.math.BigInteger;
import java.util.Stack;

public abstract class PapayaHandler {
    private final PapayaObject papayaObject;

    public PapayaHandler(PapayaObject papayaObject) {
        this.papayaObject = papayaObject;
    }

    public abstract void setMember(byte memberId[], PapayaHandler member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException;
    public abstract PapayaHandler getMember(byte memberId[], Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException;
    public abstract void call(Scope scope) throws PapayaException;

    public PapayaObject getPapayaObject() {
        return papayaObject;
    }

    public static final PapayaHandler doNothingHandler(PapayaObject object) {
        return new DefaultHandler(object);
    }

    public static final PapayaHandler readOnlyHandler(PapayaObject object) {
        return new ReadOnlyHandler(object);
    }

    public boolean asBool() {
        return papayaObject.asBool();
    }

    public BigInteger asInt() {
        return papayaObject.asInt();
    }

    public abstract PapayaHandler getAtIndex(int index) throws PapayaException;

    public abstract void setAtIndex(int index, PapayaHandler handler) throws PapayaException;

    public abstract void append(PapayaHandler handler) throws PapayaException;

    public abstract AccessModifier getModifier();

    public boolean isReadOnly() {
        return getModifier() == AccessModifier.ReadOnly;
    }
}
