package org.wolkenproject.script;

import org.wolkenproject.core.Account;
import org.wolkenproject.core.Address;
import org.wolkenproject.core.BlockStateChange;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.events.DepositFundsEvent;
import org.wolkenproject.core.events.DestroyContractEvent;
import org.wolkenproject.core.events.NewAccountEvent;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.ContractException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Contract extends SerializableI {
    public static final int IdentifierLength = 20;
    // this is generated when the contract is deployed ripemd(sha3(creator+payload)).
    private final byte  contractId[];

    public Contract(byte contractId[]) {
        this.contractId = contractId;
    }

    protected abstract <T> void onDeploy(Transaction transaction, BlockStateChange blockStateChange, T data);
    protected abstract <T> void onUpdate(Transaction transaction, BlockStateChange blockStateChange, T data);
    protected abstract void onDestroy(Transaction transaction, BlockStateChange blockStateChange, byte[] recipient);

    // this can only be called once.
    public final void deployContract(final Transaction transaction, final BlockStateChange blockStateChange, final Payload payload) throws ContractException {
        onDeploy(transaction, blockStateChange, payload);
    }

    // this will update the internal state of the contract.
    public final void updateContract(final Transaction transaction, final BlockStateChange blockStateChange, final Payload payload) throws ContractException {
        onUpdate(transaction, blockStateChange, payload);
    }

    // the contract is destroyed and all remaining funds are sent to 'recipient'.
    public final void destroyContract(final Transaction transaction, final BlockStateChange blockStateChange, final byte recipient[]) throws ContractException {
        if (recipient.length != Address.RawLength) {
            throw new ContractException("recipient must be a valid 20 byte address.");
        }

        onDestroy(transaction, blockStateChange, recipient);

        if (blockStateChange.checkAccountExists(contractId)) {
            long accountBalance = blockStateChange.getAccountBalance(contractId, true, true);

            if (accountBalance > 0) {
                if (!blockStateChange.checkAccountExists(recipient)) {
                    blockStateChange.addEvent(new NewAccountEvent(recipient));
                }

                blockStateChange.addEvent(new DepositFundsEvent(recipient, accountBalance));
            }
        }

        blockStateChange.addEvent(new DestroyContractEvent(contractId));
    }

    // returns the amount of funds the contract's 'account' has.
    public long getFunds() {
        Account account = Context.getInstance().getDatabase().findAccount(contractId);
        if (account == null) {
            return 0L;
        }

        return account.getBalance();
    }

    public byte[] getContractId() {
        return contractId;
    }

    @Override
    public final void write(OutputStream stream) throws IOException, WolkenException {
        stream.write(contractId);
        writeContract(stream);
    }

    @Override
    public final void read(InputStream stream) throws IOException, WolkenException {
        checkFullyRead(stream.read(contractId), IdentifierLength);
        readContract(stream);
    }

    protected abstract void writeContract(OutputStream stream);
    protected abstract void readContract(InputStream stream);
}
