package org.wolkenproject.papaya.runtime;

import org.wolkenproject.core.Block;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.ContractOutOfFundsExceptions;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.PapayaStructure;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Scope {
    // the 'container' block
    private BlockIndex              block;
    // the 'caller' transaction
    private Transaction             caller;
    // the contract in which the point of entry exists
    private Contract                contract;
    // the stack
    private PapayaStack<PapayaObject> stack;
    // tell all subprocesses to continue running
    private AtomicBoolean           keepRunning;
    // an interrupt signal (if any)
    private AtomicInteger           interruptSignal;
    // the program counter
    private ProgramCounter          programCounter;

    public Scope(Block block, Transaction caller, Contract contract, PapayaStack<PapayaObject> stack, ProgramCounter programCounter) {
        this.caller     = caller;
        this.contract   = contract;
        this.stack      = stack;
        this.programCounter = programCounter;
    }

    public long startProcess(long availableFee) throws InvalidTransactionException, PapayaException, ContractOutOfFundsExceptions {
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

    public PapayaStack<PapayaObject> getStack() {
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

    public void verify() throws InvalidTransactionException, PapayaException {
        if (!getStack().pop().asBool()) {
            throw new InvalidTransactionException();
        }
    }

    protected byte[] getSignatureData() {
        return null;
    }

    public void destroyContract() throws PapayaException {
        // this is the address that we will send any remaining funds to
//        PapayaObject address = getStack().pop();
//
//        if (address instanceof MochaAddress) {
//        }

        throw new PapayaException("invalid address provided.");
    }

    public Stack<PapayaStructure> getStackTrace() {
        return null;
    }
}
