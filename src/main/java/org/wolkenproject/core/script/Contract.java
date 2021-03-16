package org.wolkenproject.core.script;

import org.wolkenproject.core.Address;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.Transaction;
import org.wolkenproject.core.script.internal.MochaNumber;
import org.wolkenproject.core.script.internal.MochaObject;
import org.wolkenproject.exceptions.ContractOutOfFundsExceptions;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.exceptions.WolkenException;

public class Contract extends MochaObject {
    public Contract() {
        super(false);
    }

    public void call(int functionAddress, MochaObject...arguments) {
    }

    // create contract from a transaction payload
    // 1: any exception thrown will invalidate the contract             (r0)
    // 2: if function returns null, the contract will not be serialized (r1)
    // 3: if function returns null, the contract will not be serialized
    public static final void create(Transaction transaction, Address contractAddress, ProgramCounter programCounter, long maxSpend) throws MochaException, ContractOutOfFundsExceptions, InvalidTransactionException, WolkenException {
        // create the contract object
        Contract contract = new Contract();

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

        // create a scope
        Scope scope = new Scope(transaction, contract, stack, programCounter);

        // execute the payload
        long remaining = scope.startProcess(maxSpend);

        // check if the contract should be stored
        if (contract.shouldStoreContract()) {
            // convert contract to a byte array
            byte serializedContract[] = contract.asByteArray();

            // check the length of the contract
            long storageCost = (serializedContract.length / 32 + serializedContract.length % 32) * transaction.getMaxUnitCost();

            // store the contract
            Context.getInstance().getDatabase().storeContract(contractAddress, contract);
        }
    }

    private byte[] asByteArray() {
        return new byte[0];
    }

    private boolean shouldStoreContract() {
        return false;
    }
}
