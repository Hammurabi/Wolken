package org.wolkenproject.core.transactions;

import org.wolkenproject.core.*;
import org.wolkenproject.core.events.DepositFundsEvent;
import org.wolkenproject.core.events.RegisterAliasEvent;
import org.wolkenproject.core.events.WithdrawFundsEvent;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class RegisterAliasTransaction extends Transaction {
    // nonce
    private long nonce;
    // alias
    private long alias;
    // signature of the sender
    private RecoverableSignature signature;

    private RegisterAliasTransaction() {
        this(0, 0);
    }

    private RegisterAliasTransaction(long nonce, long alias) {
        this.nonce = nonce;
        this.alias = alias;
        this.signature = new RecoverableSignature();
    }

    @Override
    public int getFlags() {
        return 0;
    }

    @Override
    public long getTransactionValue() {
        return Context.getInstance().getNetworkParameters().getAliasRegistrationCost();
    }

    @Override
    public long getTransactionFee() {
        return 0;
    }

    @Override
    public long getMaxUnitCost() {
        return 0;
    }

    @Override
    public byte[] getPayload() {
        return new byte[0];
    }

    @Override
    public boolean shallowVerify() throws WolkenException {
        // this is not 100% necessary
        // a transfer of 0 with a fee of 0 is not allowed
        return
                (Context.getInstance().getDatabase().getAccount(getSender().getRaw()).getNonce() + 1) == nonce &&
                        (signature.getR().length == 32) &&
                        (signature.getS().length == 32) &&
                        getSender() != null &&
                        !Context.getInstance().getDatabase().getAccount(getSender().getRaw()).hasAlias();
    }

    @Override
    public Address getSender() throws WolkenException {
        return Address.fromKey(signature.recover(asByteArray()));
    }

    @Override
    public Address getRecipient() {
        return null;
    }

    @Override
    public boolean hasMultipleSenders() {
        return false;
    }

    @Override
    public boolean hasMultipleRecipients() {
        return false;
    }

    @Override
    public long calculateSize() {
        return
                VarInt.sizeOfCompactUin32(getVersion(), false) +
                        VarInt.sizeOfCompactUin64(nonce, false) +
                        VarInt.sizeOfCompactUin64(alias, false) + 65;
    }

    @Override
    public boolean verify(Block block, int blockHeight, long fees) {
        return false;
    }

    @Override
    public void getStateChange(Block block, int blockHeight, BlockStateChange stateChange) throws WolkenException {
        Address sender = getSender();
        createAccountIfDoesNotExist(sender.getRaw(), stateChange);
        stateChange.addEvent(new RegisterAliasEvent(sender.getRaw(), alias));
    }

    @Override
    protected void setSignature(Signature signature) throws WolkenException {
        if (signature instanceof RecoverableSignature) {
            this.signature = (RecoverableSignature) signature;
        }

        throw new WolkenException("invalid signature type '" + signature.getClass() + "'.");
    }

    @Override
    protected Transaction copyForSignature() {
        return new RegisterAliasTransaction(nonce, alias);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        signature.write(stream);
        VarInt.writeCompactUInt64(alias, false, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        signature.read(stream);
        alias = VarInt.readCompactUInt64(false, stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RegisterAliasTransaction();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(MintTransaction.class);
    }
}