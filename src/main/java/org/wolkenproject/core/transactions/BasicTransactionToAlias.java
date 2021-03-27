package org.wolkenproject.core.transactions;

import org.json.JSONObject;
import org.wolkenproject.core.Address;
import org.wolkenproject.core.Block;
import org.wolkenproject.core.BlockStateChange;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.events.DepositFundsEvent;
import org.wolkenproject.core.events.WithdrawFundsEvent;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// this is a basic transaction
// min size: 1 + 69
// avg size: 1 + 82
// max size: 1 + 97
public class BasicTransactionToAlias extends Transaction {
    // must be a valid 1-8 byte alias to address
    // if alias does not exist, the transaction
    // will be rejected
    private long alias;
    // value of the transfer
    private long value;
    // maximum fee that sender is willing to pay
    private long fee;
    // transaction index
    private long nonce;
    // a recoverable ec signature
    private RecoverableSignature signature;

    public BasicTransactionToAlias() {
        this(0, 0, 0, 0);
    }

    public BasicTransactionToAlias(long alias, long value, long fee, long nonce) {
        this.alias      = alias;
        this.value      = value;
        this.fee        = fee;
        this.nonce      = nonce;
        this.signature = new RecoverableSignature();
    }

    @Override
    public int getFlags() {
        return 0;
    }

    @Override
    public long getTransactionValue() {
        return value;
    }

    @Override
    public long getTransactionFee() {
        return fee;
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
    public boolean shallowVerify() {
        // a transfer of 0 with a fee of 0 is not allowed
        try {
            return
                    getTransactionValue() >= 0 &&
                            getTransactionFee() >= 0 &&
                            //possible vulnerability with a+b!=0 using signed integers
                            (getTransactionValue() + getTransactionFee()) > 0 &&
                            (signature.getR().length == 32) &&
                            (signature.getS().length == 32) &&
                            getSender() != null &&
                            (Context.getInstance().getDatabase().findAccount(getSender().getRaw()).getNonce() + 1) == nonce &&
                            (Context.getInstance().getDatabase().findAccount(getSender().getRaw()).getBalance()) >= (value + fee);
        } catch (WolkenException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Address getSender() throws WolkenException {
        return Address.fromKey(signature.recover(asByteArray()));
    }

    @Override
    public Address getRecipient() {
        return Context.getInstance().getDatabase().getAddressFromAlias(alias);
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
        return VarInt.sizeOfCompactUin32(getVersion(), false) +
                VarInt.sizeOfCompactUin64(alias, false) +
                VarInt.sizeOfCompactUin64(value, false) +
                VarInt.sizeOfCompactUin64(fee, false) +
                VarInt.sizeOfCompactUin64(nonce, false) +
                65;
    }

    @Override
    public boolean verify(Block block, int blockHeight, long fees) {
        return false;
    }

    @Override
    public void getStateChange(Block block, int blockHeight, BlockStateChange stateChange) throws WolkenException {
        stateChange.addEvent(new DepositFundsEvent(getRecipient().getRaw(), value));
        stateChange.addEvent(new WithdrawFundsEvent(getSender().getRaw(), value));
    }

    @Override
    public JSONObject toJson(boolean txEvt, boolean evHash) {
        return null;
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
        return new BasicTransactionToAlias(alias, value, fee, nonce);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        VarInt.writeCompactUInt64(alias, false, stream);
        VarInt.writeCompactUInt64(value, false, stream);
        VarInt.writeCompactUInt64(fee, false, stream);
        VarInt.writeCompactUInt64(nonce, false, stream);
        signature.write(stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        alias   = VarInt.readCompactUInt64(false, stream);
        value   = VarInt.readCompactUInt64(false, stream);
        fee     = VarInt.readCompactUInt64(false, stream);
        nonce   = VarInt.readCompactUInt64(false, stream);
        signature.read(stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new BasicTransaction();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(BasicTransactionToAlias.class);
    }
}
