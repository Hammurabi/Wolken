package org.wolkenproject.core.transactions;

import org.json.JSONObject;
import org.wolkenproject.core.*;
import org.wolkenproject.core.events.DepositFundsEvent;
import org.wolkenproject.core.events.WithdrawFundsEvent;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class BasicTransaction extends Transaction {
    // must be a valid 20 byte address hash160(hash256(publicKey))
    private byte recipient[];
    // value of the transfer
    private long value;
    // maximum fee that sender is willing to pay
    private long fee;
    // transaction index
    private long nonce;
    // a recoverable ec signature
    private RecoverableSignature signature;

    public BasicTransaction() {
        this(new byte[Address.RawLength], 0, 0, 0);
    }

    public BasicTransaction(byte recipient[], long value, long fee, long nonce) {
        this.recipient  = recipient;
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
        }

        return false;
    }

    @Override
    public Address getSender() throws WolkenException {
        return Address.fromKey(signature.recover(asByteArray()));
    }

    @Override
    public Address getRecipient() {
        return Address.fromRaw(recipient);
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
        return VarInt.sizeOfCompactUin32(getVersion(), false) + 20 +
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
        Address sender = getSender();
        stateChange.createAccountIfDoesNotExist(recipient);
        stateChange.addEvent(new DepositFundsEvent(recipient, value));
        stateChange.addEvent(new WithdrawFundsEvent(sender.getRaw(), value + fee));
    }

    @Override
    public JSONObject toJson(boolean txEvt, boolean evHash) {
        JSONObject txHeader = new JSONObject().put("name", getClass().getName()).put("version", getVersion());
        txHeader.put("content", new JSONObject().put("recipient", Base58.encode(recipient))).put("value", value).put("fee", fee).put("nonce", nonce).put("v", signature.getV()).put("r", Base16.encode(signature.getR())).put("s", Base16.encode(signature.getS()));
        return txHeader;
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
        return new BasicTransaction(Arrays.copyOf(recipient, recipient.length),value,fee,nonce);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        stream.write(recipient);
        VarInt.writeCompactUInt64(value, false, stream);
        VarInt.writeCompactUInt64(fee, false, stream);
        VarInt.writeCompactUInt64(nonce, false, stream);
        signature.write(stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        checkFullyRead(stream.read(recipient), 20);
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
        return Context.getInstance().getSerialFactory().getSerialNumber(BasicTransaction.class);
    }
}
