package org.wolkenproject.core.script;

import org.wolkenproject.core.TransactionI;
import org.wolkenproject.core.script.internal.MochaECSig;
import org.wolkenproject.core.script.internal.MochaObject;
import org.wolkenproject.core.script.internal.MochaECPubKey;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Scope {
    // the 'caller' transaction
    private TransactionI            caller;
    // the input index which we are calling from
    private int                     callerIdx;
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

    public Scope(TransactionI caller, int callerIdx, Contract contract, MochaStack<MochaObject> stack, ProgramCounter programCounter) {
        this.caller     = caller;
        this.callerIdx  = callerIdx;
        this.contract   = contract;
        this.stack      = stack;
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

    public void checkSig() throws MochaException {
        MochaObject publicKey = getStack().pop();
        MochaObject signature = getStack().pop();
        byte signatureData[]  = getSignatureData();

        if (publicKey instanceof MochaECPubKey) {
            getStack().push(new MochaBool(false));
            return;
        }

        if (signature instanceof MochaECSig) {
            getStack().push(new MochaBool(false));
            return;
        }

        getStack().push(((MochaECPubKey) publicKey).verifySignature((MochaECSig) signature));
    }

    public void verify() throws InvalidTransactionException, MochaException {
        if (!getStack().pop().isTrue()) {
            throw new InvalidTransactionException();
        }
    }

    protected byte[] getSignatureData() {
        return null;
    }
}
