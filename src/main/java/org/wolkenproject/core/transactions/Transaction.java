package org.wolkenproject.core.transactions;

import org.json.JSONObject;
import org.wolkenproject.core.*;
import org.wolkenproject.core.events.*;
import org.wolkenproject.crypto.Keypair;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.serialization.SerializationFactory;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Transaction extends SerializableI implements Comparable<Transaction> {
    public static int UniqueIdentifierLength = 32;

    public static final class Flags
    {
        public static final int
                NoFlags             = 0,
                TwoByteFlags        = 1,
                MochaPayload        = 1<<1,
                MultipleRecipients  = 1<<2,
                UseAliases          = 1<<3,
                UnusedFlag1         = 1<<4,
                UnusedFlag2         = 1<<5,
                UnusedFlag3         = 1<<5,
                UnusedFlag4         = 1<<6,
                UnusedFlag5         = 1<<7,
                UnusedFlag6         = 1<<8
        ;
    }

    // this is not serialized
    private byte txid[];

    // can be represented by 1 - 4 bytes
    // version = 1 skips flags all-together

    // this should not be treated as a network version
    // transaction versions should be treated as VARINT
    // magic numbers that hint at the internal transaction
    // structure.
    // anything below here is optional
    public int getVersion() {
        return getSerialNumber();
    }

    public abstract int getFlags();
    public abstract long getTransactionValue();
    public abstract long getTransactionFee();
    public abstract long getMaxUnitCost();
    public abstract byte[] getPayload();
    /*
        shallow checks of the validity of a transactions
        check the receiver is valid
        check the sender is valid
        check the signature is valid
        check the sender has funds
     */
    public abstract boolean shallowVerify();
    public abstract Address getSender() throws WolkenException;
    public abstract Address getRecipient();
    public abstract boolean hasMultipleSenders();
    public abstract boolean hasMultipleRecipients();
    public abstract long calculateSize();
    /*
        deep checks of the validity of a transactions
        if the transaction contains a payload, the pa
        -yload would be executed and if errors are th
        -rown without being caught then the transacti
        -on is deemed invalid.
     */
    public abstract boolean verify(Block block, int blockHeight, long fees);
    /*
        return all the changes this transaction will
        cause to the global state.
     */
    public abstract void getStateChange(Block block, int blockHeight, BlockStateChange stateChange) throws WolkenException;

    public JSONObject toJson() {
        return toJson(true);
    }

    public abstract JSONObject toJson(boolean txEvt);

    public Transaction sign(Keypair keypair) throws WolkenException {
        // this includes the version bytes
        byte tx[] = asSerializedArray();
        Signature signature = keypair.sign(tx);
        Transaction transaction = copyForSignature();
        transaction.setSignature(signature);

        return transaction;
    }

    protected abstract void setSignature(Signature signature) throws WolkenException;

    protected abstract Transaction copyForSignature();

    // multiple recipients and senders might be possible in the future
    public Address[] getSenders() throws WolkenException {
        return new Address[] { getSender() };
    }
    public Address[] getRecipients() {
        return new Address[] { getRecipient() };
    }

    public byte[] getHash() {
        if (txid == null) {
            txid = HashUtil.sha256d(asByteArray());
        }

        return txid;
    }

    @Override
    public int compareTo(Transaction transaction) {
        return (getMaxUnitCost() > transaction.getMaxUnitCost() ? 1 : -1);
    }

    // a transaction that creates a contract
    public static Transaction newPayload(long amount, long fee, long nonce, byte payLoad[]) {
        return new PayloadTransaction(amount, fee, nonce, payLoad);
    }

    // a purely monetary transaction
    public static Transaction newTransfer(Address recipient, long amount, long fee, long nonce) {
        return new BasicTransaction(recipient.getRaw(), amount, fee, nonce);
    }

    public static Transaction newMintTransaction(String msg, long reward, Address addresses) {
        return new MintTransaction(reward, addresses.getRaw(), msg.getBytes());
    }

    public static final void register(SerializationFactory factory) {
        factory.registerClass(MintTransaction.class, new MintTransaction());
        factory.registerClass(BasicTransaction.class, new BasicTransaction());
        factory.registerClass(BasicTransactionToAlias.class, new BasicTransactionToAlias());
        factory.registerClass(RegisterAliasTransaction.class, new RegisterAliasTransaction());
        factory.registerClass(PayloadTransaction.class, new PayloadTransaction());
    }

    // this is a basic transaction
    // min size: 1 + 69
    // avg size: 1 + 82
    // max size: 1 + 97
    public static final class BasicTransactionToAlias extends Transaction {
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
                        (Context.getInstance().getDatabase().getAccount(getSender().getRaw()).getNonce() + 1) == nonce &&
                        (Context.getInstance().getDatabase().getAccount(getSender().getRaw()).getBalance()) >= (value + fee);
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

    // this is a basic payload transaction (contract creation)
    // transfer value is sent to the contract's account
    // min size: 1 + 68 + (varint) + payload
    // avg size: 1 + 79 + (varint) + payload
    // max size: 1 + 89 + (varint) + payload
    public static final class PayloadTransaction extends Transaction {
        // not serialized
        private List<Event> stateChangeEvents;
        // value of the transfer
        private long value;
        // maximum amount that sender is willing to pay
        private long fee;
        // maximum fee that sender is willing to pay
        private long unitCost;
        // transaction index
        private long nonce;
        // a recoverable ec signature
        private RecoverableSignature signature;
        // a valid mocha payload
        private byte payload[];

        public PayloadTransaction() {
            this(0, 0, 0, new byte[0]);
        }

        public PayloadTransaction(long value, long fee, long nonce, byte payload[]) {
            this.value  = value;
            this.fee    = fee;
            this.nonce  = nonce;
            this.payload= payload;
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
            return unitCost;
        }

        @Override
        public byte[] getPayload() {
            return new byte[0];
        }

        @Override
        public boolean shallowVerify() {
            return false;
        }

        @Override
        public Address getSender() throws WolkenException {
            return Address.fromKey(signature.recover(asByteArray()));
        }

        @Override
        public Address getRecipient() {
            try {
                return Address.newContractAddress(getSender().getRaw(), nonce);
            } catch (WolkenException e) {
                return null;
            }
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
                    VarInt.sizeOfCompactUin64(value, false) +
                    VarInt.sizeOfCompactUin64(unitCost, false) +
                    VarInt.sizeOfCompactUin64(fee, false) +
                    VarInt.sizeOfCompactUin64(nonce, false) +
                    VarInt.sizeOfCompactUin64(payload.length, false) +
                    payload.length +
                    65;
        }

        @Override
        public boolean verify(Block block, int blockHeight, long fees) {
            return false;
        }

        @Override
        public void getStateChange(Block block, int blockHeight, BlockStateChange stateChange) throws WolkenException {
            if (stateChangeEvents == null) {
                stateChangeEvents = new ArrayList<>();
            }

            stateChange.addEvents(stateChangeEvents);
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
            return new PayloadTransaction(value, fee, nonce, Arrays.copyOf(payload, payload.length));
        }

        @Override
        public void write(OutputStream stream) throws IOException, WolkenException {
            VarInt.writeCompactUInt64(value, false, stream);
            VarInt.writeCompactUInt64(fee, false, stream);
            VarInt.writeCompactUInt64(nonce, false, stream);
            signature.write(stream);
            VarInt.writeCompactUInt32(payload.length, false, stream);
            stream.write(payload);
        }

        @Override
        public void read(InputStream stream) throws IOException, WolkenException {
            value   = VarInt.readCompactUInt64(false, stream);
            fee     = VarInt.readCompactUInt64(false, stream);
            nonce   = VarInt.readCompactUInt64(false, stream);
            signature.read(stream);
            int length = VarInt.readCompactUInt32(false, stream);
            if (length > 0) {
                payload = new byte[length];
                checkFullyRead(stream.read(payload), length);
            }
        }

        @Override
        public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
            return (Type) new PayloadTransaction();
        }

        @Override
        public int getSerialNumber() {
            return Context.getInstance().getSerialFactory().getSerialNumber(PayloadTransaction.class);
        }
    }

    // this is a modular transaction
    // it should be possible to use
    // flags to enable/disable specific
    // functionality
    public static final class FlaggedTransaction extends Transaction {
        // can be represented by 1 or more bytes
        // there are not enough flags at the moment
        // therefore it's represented by an int in
        // this version.
        private int flags;

        @Override
        public int getFlags() {
            return 0;
        }

        @Override
        public long getTransactionValue() {
            return 0;
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
        public boolean shallowVerify() {
            return false;
        }

        @Override
        public Address getSender() {
            return null;
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
            return 0;
        }

        @Override
        public boolean verify(Block block, int blockHeight, long fees) {
            return false;
        }

        @Override
        public void getStateChange(Block block, int blockHeight, BlockStateChange stateChange) throws WolkenException {
        }

        @Override
        protected void setSignature(Signature signature) throws WolkenException {
        }

        @Override
        protected Transaction copyForSignature() {
            return null;
        }

        @Override
        public void write(OutputStream stream) throws IOException, WolkenException {
        }

        @Override
        public void read(InputStream stream) throws IOException, WolkenException {
        }

        @Override
        public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
            return null;
        }

        @Override
        public int getSerialNumber() {
            return -1;
        }
    }
}
