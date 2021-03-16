package org.wolkenproject.core.script;

import org.wolkenproject.core.Transaction;
import org.wolkenproject.core.script.internal.MochaObject;
import org.wolkenproject.exceptions.ContractOutOfFundsExceptions;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Scope {
    // the 'caller' transaction
    private Transaction             caller;
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

    public Scope(Transaction caller, Contract contract, MochaStack<MochaObject> stack, ProgramCounter programCounter) {
        this.caller     = caller;
        this.contract   = contract;
        this.stack      = stack;
        this.programCounter = programCounter;
    }

    public long startProcess(long availableFee) throws InvalidTransactionException, MochaException, ContractOutOfFundsExceptions {
        while (getProgramCounter().hasNext() && keepRunning.get()) {
            Opcode opcode = getProgramCounter().next();

            if (availableFee >= opcode.getWeight()) {
                opcode.execute(this);
                availableFee -= opcode.getWeight();
                continue;
            }

            throw new ContractOutOfFundsExceptions();
        }

        return availableFee;
    }

    public void stopProcesses(int signal) {
        keepRunning.set(false);
        interruptSignal.set(signal);
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

//    public void checkSig() throws MochaException {
//        MochaObject address     = getStack().pop();
//        MochaObject signature   = getStack().pop();
//        byte signatureData[]    = getSignatureData();
//
//        Key key                 = ((MochaCryptoSignature) signature).getSignature().checkSignature(signatureData);
//
//        if (signature instanceof MochaCryptoSignature) {
//            getStack().push(new MochaBool(false));
//            return;
//        }
//
//        getStack().push(((MochaPublicKey) publicKey).checkSignature((MochaCryptoSignature) signature, signatureData));
//    }

    public void verify() throws InvalidTransactionException, MochaException {
        if (!getStack().pop().isTrue()) {
            throw new InvalidTransactionException();
        }
    }

    protected byte[] getSignatureData() {
        return null;
    }
}
