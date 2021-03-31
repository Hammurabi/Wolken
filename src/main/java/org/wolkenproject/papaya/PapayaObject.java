package org.wolkenproject.papaya;

import org.wolkenproject.papaya.compiler.PapayaStructure;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.exceptions.WolkenException;

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
    private Map<Integer, PapayaHandler> members;

    public void setMember(int memberId, PapayaHandler member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        structure.checkWriteAccess(memberId, stackTrace);
        members.put(memberId, member);
    }

    public PapayaHandler getMember(int memberId, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        structure.checkReadAccess(memberId, stackTrace);
        return members.get(memberId);
    }

    public void call(Scope scope) throws WolkenException {
        callable.call(scope);
    }
}
