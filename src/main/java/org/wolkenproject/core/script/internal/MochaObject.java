package org.wolkenproject.core.script.internal;

import org.wolkenproject.core.script.MochaFunction;

public class MochaObject {
    private MochaFunction functions[];

    public MochaObject add(MochaObject other) { return this; }
    public MochaObject sub(MochaObject other) { return this; }
    public MochaObject mul(MochaObject other) { return this; }
    public MochaObject div(MochaObject other, boolean sign) { return this; }
    public MochaObject mod(MochaObject other, boolean sign) { return this; }
    public MochaObject shiftRight(MochaObject other, boolean sign) { return this; }
    public MochaObject shiftLeft(MochaObject other, boolean sign) { return this; }
    public MochaObject arithmeticShift(MochaObject other, boolean sign) { return this; }
    public MochaObject equal(MochaObject other) { return this; }
    public MochaObject and(MochaObject other) { return this; }
    public MochaObject or(MochaObject other) { return this; }
    public MochaObject xor(MochaObject other) { return this; }
    public MochaObject not() { return this; }
    public MochaObject negate() { return this; }
    public MochaObject length() { return this; }
    public MochaObject shape() { return this; }
    public MochaObject reshape(MochaObject shape) { return this; }
    public MochaObject append(MochaObject object) { return this; }
    public MochaObject prepend(MochaObject object) { return this; }
    public MochaObject pop() { return this; }
    public MochaObject poll() { return this; }
}
