package org.wolkenproject.core.script.internal;

import org.wolkenproject.core.script.MochaFunction;

public class MochaObject {
    public static final int
    FN_ADD = 0,
    FN_SUB = 1,
    FN_MUL = 2,
    FN_DIV = 3,
    FN_MOD = 4,
    FN_SHR = 5,
    FN_SHL = 6,
    FN_ASH = 7,
    FN_EQL = 8,
    FN_AND = 9,
    FN_OR = 10,
    FN_XOR = 11,
    FN_NOT = 12,
    FN_NGT = 13;
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
}
