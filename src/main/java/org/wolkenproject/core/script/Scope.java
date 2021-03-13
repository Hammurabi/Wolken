package org.wolkenproject.core.script;

import org.wolkenproject.core.script.internal.MochaObject;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;

import java.nio.ByteBuffer;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Scope {
    // the contract in which the point of entry exists
    private Contract                contract;
    // the stack
    private MochaStack<MochaObject> stack;
    // tell all subprocesses to continue running
    private AtomicBoolean           keepRunning;
    // an interrupt signal (if any)
    private AtomicInteger           interruptSignal;
    // the program counter
    private ProgramCounter          programCounter;

    public Scope(VirtualMachine virtualMachine, Contract contract, Stack<MochaObject> stack, ProgramCounter programCounter) {
        this.contract   = contract;
        this.stack      = new MochaStack<>();
        this.programCounter = programCounter;
    }

    public Contract getContract() {
        return contract;
    }

    public MochaStack<MochaObject> getStack() {
        return stack;
    }

    public ProgramCounter getProgramCounter() {
        return programCounter;
    }

    public void stopProcesses(int signal) {
        keepRunning.set(false);
        interruptSignal.set(signal);
    }

    public void verify() throws InvalidTransactionException, MochaException {
        if (!getStack().pop().isTrue()) {
            throw new InvalidTransactionException();
        }
    }
}
