package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.PapayaMember;
import org.wolkenproject.papaya.compiler.PapayaStructure;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.exceptions.WolkenException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/*
    PapayaObjects do not expose the underlying memory structure
    for safety reasons, therefore, children of objects are accessible
    by <32Bit> identifiers, it also allows implementation specific access
    to object children.
 */
public class PapayaObject {
    /*
        A pointer to a structure object which holds information
        about the object, number of fields and functions, etc.
     */
    private PapayaStructure structure;
    /*
        A callable object.
     */
    private PapayaCallable callable;
    /*
        Using a map structure allows us to call members by name
        ie: object.member = .. ===> object.map(hash('member')) = ..
        this allows us to DYNAMICALLY add members to an object.
     */
    private Map<byte[], PapayaObject> members;

    public PapayaObject() {
        this(PapayaCallable.Default);
    }

    public PapayaObject(PapayaCallable callable) {
        this.callable = callable;
        this.members = new HashMap<>();
    }

    public void setMember(byte memberId[], PapayaHandler member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        PapayaMember classMember = structure.getMember(memberId);
        structure.checkWriteAccess(classMember, stackTrace);
        members.put(memberId, member.getPapayaObject());
    }

    public PapayaHandler getMember(byte memberId[], Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        PapayaMember classMember = structure.getMember(memberId);
        return structure.checkMemberAccess(classMember, members.get(memberId), stackTrace);
    }

    public void call(Scope scope) throws PapayaException {
        callable.call(scope);
    }

    public boolean asBool() {
        return false;
    }

    public BigInteger asInt() {
        return BigInteger.ZERO;
    }

    public PapayaContainer asContainer() throws PapayaException {
        throw new PapayaException("'"+structure.getName()+"' is not a container.");
    }
}
