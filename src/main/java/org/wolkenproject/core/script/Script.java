package org.wolkenproject.core.script;

import org.wolkenproject.core.Address;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.Transaction;
import org.wolkenproject.core.script.internal.MochaNumber;
import org.wolkenproject.core.script.internal.MochaObject;
import org.wolkenproject.exceptions.ContractOutOfFundsExceptions;

public abstract class Script {
    public static byte[] newP2PKH(Address address) {
        return new byte[0];
    }

    public abstract void fromCompressedFormat(byte data[]);
    public abstract byte[] getCompressed();

    public static void executePayload(Transaction transaction) {
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
        long maxSpend = transaction.getMaxUnitCost() * transaction.getTransactionFee();

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

            if (remaining >= storageCost) {
                // store the contract
                Context.getInstance().getDatabase().storeContract(contractAddress, serializedContract);
            }

            throw new ContractOutOfFundsExceptions();
        }
    }
}
