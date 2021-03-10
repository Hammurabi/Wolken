package org.wolkenproject.core.script.internal;

import org.wolkenproject.core.script.VirtualProcess;
import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.Utils;

import java.util.Arrays;

public class MochaObject {
    private static final MochaObject fn_add = createFunction((proc)->{ return null; });
    private MochaObject     members[];
    private MochaCallable   callable;

    public MochaObject() {
        this((proc)->{ return null; });
    }

    public MochaObject(MochaCallable callble) {
        members     = new MochaObject[0];
        callable    = callble;
        addMember(fn_add);
    }

    public static final MochaObject createFunction(MochaCallable callable) {
        return new MochaObject(callable);
    }

    public static final MochaObject createObject(MochaCallable callable) {
        MochaObject object = new MochaObject(callable);
        object.addMember(createFunction((proc)->{ return null; })); // add
        object.addMember(createFunction((proc)->{ return null; })); // sub
        object.addMember(createFunction((proc)->{ return null; })); // mul
        object.addMember(createFunction((proc)->{ return null; })); // div
        object.addMember(createFunction((proc)->{ return null; })); // mod

        return object;
    }

    protected int addMember(MochaObject member) {
        int len = members.length;
        members = Arrays.copyOf(members, members.length + 1);
        members[len] = member;

        return len;
    }

    protected void addMemberToFront(MochaObject member) {
        members = Utils.prepend(member, members);
    }

    public void isCallable() {
    }

    public final MochaObject call(VirtualProcess process) {
        return callable.call(process);
    }

    public MochaObject add(MochaObject other) throws MochaException { return this; }
    public MochaObject sub(MochaObject other) throws MochaException { return this; }
    public MochaObject mul(MochaObject other) throws MochaException { return this; }
    public MochaObject div(MochaObject other, boolean sign) throws MochaException { return this; }
    public MochaObject mod(MochaObject other, boolean sign) throws MochaException { return this; }
    public MochaObject shiftRight(MochaObject other, boolean sign) throws MochaException { return this; }
    public MochaObject shiftLeft(MochaObject other, boolean sign) throws MochaException { return this; }
    public MochaObject arithmeticShift(MochaObject other, boolean sign) throws MochaException { return this; }
    // equals (x==y)
    public MochaObject equal(MochaObject other) throws MochaException { return this; }
    // and (x&y)
    public MochaObject and(MochaObject other) { return this; }
    // or (x|y)
    public MochaObject or(MochaObject other) { return this; }
    // xor (x^y)
    public MochaObject xor(MochaObject other) { return this; }
    // not (!x)
    public MochaObject not() { return this; }
    // negate (~x)
    public MochaObject negate() { return this; }
    // get the length of an array
    public MochaObject length() { return new MochaNumber(members.length, false); }
    // get the shape of an array
    public MochaObject shape() { return this; }
    // reshape an array
    public MochaObject reshape(MochaObject shape) { return this; }
    // add an element to the back
    public MochaObject append(MochaObject object) { addMember(object); return this; }
    // add an element to the front
    public MochaObject prepend(MochaObject object) { addMemberToFront(object); return this; }
    // pop the last element
    public MochaObject pop() throws MochaException {
        if (members.length > 0) {
            MochaObject last = members[members.length - 1];
            members = new MochaObject[members.length - 1];
            return last;
        }

        throw new MochaException("cannot pop() from empty array.");
    }
    // poll the first element
    public MochaObject poll() throws MochaException {
        if (members.length > 0) {
            MochaObject first = members[0];
            MochaObject temp[] = members;
            members = new MochaObject[members.length - 1];

            for (int i = 0; i < members.length; i ++) {
                members[i] = temp[i + 1];
            }

            return first;
        }

        throw new MochaException("cannot poll() from empty array.");
    }
}
