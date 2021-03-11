package org.wolkenproject.core.script;

import org.wolkenproject.core.script.internal.MochaObject;

import java.util.Stack;

public class Scope {
    // the contract in which the point of entry exists
    private Contract            contract;
    // the stack
    private Stack<MochaObject>  stack;

    public Scope(VirtualMachine virtualMachine, Contract contract, Stack<MochaObject> stack) {
        this.contract   = contract;
        this.stack      = stack;
    }

    public Contract getContract() {
        return contract;
    }

    public Stack<MochaObject> getStack() {
        return stack;
    }
}
