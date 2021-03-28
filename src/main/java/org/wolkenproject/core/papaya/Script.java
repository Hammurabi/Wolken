package org.wolkenproject.core.papaya;

import org.wolkenproject.core.Address;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.core.papaya.internal.MochaNumber;
import org.wolkenproject.core.papaya.internal.MochaObject;
import org.wolkenproject.exceptions.ContractOutOfFundsExceptions;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;

import java.nio.ByteBuffer;

public abstract class Script {
    public static byte[] newP2PKH(Address address) {
        return new byte[0];
    }

    public abstract void fromCompressedFormat(byte data[]);
    public abstract byte[] getCompressed();

    public static long executePayload(Transaction transaction) throws ContractOutOfFundsExceptions, InvalidTransactionException, MochaException {
        // create a program counter from opcodes
        ProgramCounter programCounter = new ProgramCounter(ByteBuffer.wrap(transaction.getPayload()), Context.getInstance().getOpcodeRegister());

        // create the contract object
        MochaObject contract = Contract.newContract(transaction.getRecipient());

        // create the transaction object
        MochaObject transactionObject = new MochaObject(false);
        transactionObject.addMember(new MochaBool(transaction.hasMultipleSenders()));
        transactionObject.addMember(new MochaBool(transaction.hasMultipleRecipients()));
        transactionObject.addMember(new MochaNumber(transaction.getVersion(), false));
        transactionObject.addMember(new MochaNumber(transaction.getTransactionValue(), false));
        transactionObject.addMember(new MochaNumber(transaction.getTransactionFee(), false));

        // create the stack and populate it
        MochaStack<MochaObject> stack = new MochaStack<>();
        stack.push(contract);
        stack.push(transactionObject);

        // maximum amount to spend
        long maxSpend = transaction.getTransactionFee();

        // create a scope
//        Scope scope = new Scope(transaction, contract, stack, programCounter);
//
//        // execute the payload
//        long remaining = scope.startProcess(maxSpend);
//
//        // check if the contract should be stored
//        if (contract.getMember(0)) {
//            // convert contract to a byte array
//            byte serializedContract[] = contract.asByteArray();
//
//            // check the length of the contract
//            long wordSize    = 32;
//            long storageCost = (serializedContract.length / wordSize) + Math.min(serializedContract.length % wordSize, 1) * wordSize;
//
//            if (remaining >= storageCost) {
//                // store the contract
//                Context.getInstance().getDatabase().storeContract(contractAddress, serializedContract);
//                remaining -= storageCost;
//            } else {
//                throw new ContractOutOfFundsExceptions();
//            }
//        }

        // return the amount spent
//        return maxSpend - remaining;

        return 0;
    }
}
