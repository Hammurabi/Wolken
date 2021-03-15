package org.wolkenproject.core;

import org.wolkenproject.core.script.Script;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.serialization.SerializationFactory;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public abstract byte[] getPayload();
    public abstract boolean verify();
    public abstract Address getSender() throws WolkenException;
    public abstract Address getRecipient();

    // multiple recipients and senders might be possible in the future
    public Address[] getSenders() throws WolkenException {
        return new Address[] { getSender() };
    }
    public Address[] getRecipients() {
        return new Address[] { getRecipient() };
    }

    public byte[] getTransactionID() {
        return HashUtil.sha256d(asByteArray());
    }

    @Override
    public int compareTo(Transaction transaction) {
        return (getTransactionFee() > transaction.getTransactionFee() ? 1 : -1);
    }


    // a transaction that creates a contract
    public static Transaction newPayload(long amount, byte payLoad[]) {
        return new Transaction();
    }

    // a purely monetary transaction
    public static Transaction newTransfer(Address recipient, long amount, long fee) {
        return new BasicTransaction(recipient, amount, fee);
    }

    public static Transaction newCoinbase(int blockHeight, String msg, long reward, Address addresses[]) {
        Input inputs[] = { new Input(new byte[UniqueIdentifierLength], 0, Utils.concatenate(Utils.takeApart(blockHeight), msg.getBytes())) };
        Output outputs[] = new Output[addresses.length];

        long rewardPerAddress   = reward / addresses.length;
        long change             = reward - (addresses.length * rewardPerAddress);

        for (int i = 0; i < addresses.length; i ++)
        {
            long outputValue = rewardPerAddress;
            if (i == 0)
            {
                outputValue += change;
            }

            outputs[i] = new Output(outputValue, Script.newP2PKH(addresses[i]));
        }
    }

    public static final void register(SerializationFactory factory) {
        factory.registerClass(MintTransaction.class, new MintTransaction());
        factory.registerClass(BasicTransaction.class, new BasicTransaction());
        factory.registerClass(PayloadTransaction.class, new PayloadTransaction());
//        factory.registerClass(FlaggedTransaction.class, new FlaggedTransaction());
    }

    public static final class MintTransaction extends Transaction {
        // the amount of coins to be minted
        private long value;
        // the recipient
        private byte recipient[];
        // a small data-dump for the miner
        private byte dump[];

        private MintTransaction() {
            this(0, new byte[Address.RawLength]);
        }

        private MintTransaction(long value, byte recipient[]) {
            this.value = value;
            this.recipient = recipient;
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
            return 0;
        }

        @Override
        public byte[] getPayload() {
            return new byte[0];
        }

        @Override
        public boolean verify() {
            // this is not 100% necessary
            return dump.length <= 8192;
        }

        @Override
        public Address getSender() throws WolkenException {
            return null;
        }

        @Override
        public Address getRecipient() {
            return Address.fromRaw(recipient);
        }

        @Override
        public void write(OutputStream stream) throws IOException, WolkenException {
            VarInt.writeCompactUInt64(value, false, stream);
            stream.write(recipient);
            VarInt.writeCompactUInt32(dump.length, false, stream);
            if (dump.length > 0) {
                stream.write(dump);
            }
        }

        @Override
        public void read(InputStream stream) throws IOException, WolkenException {
            value = VarInt.readCompactUInt64(false, stream);
            stream.read(recipient);
            int length = VarInt.readCompactUInt32(false, stream);
            if (length > 0) {
                checkFullyRead(stream.read(dump), length);
            }
        }

        @Override
        public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
            return (Type) new MintTransaction();
        }

        @Override
        public int getSerialNumber() {
            return Context.getInstance().getSerialFactory().getSerialNumber(MintTransaction.class);
        }
    }

    // this is a basic transaction
    // min size: 1 + 87
    // avg size: 1 + 97
    // max size: 1 + 101
    public static final class BasicTransaction extends Transaction {
        // must be a valid 20 byte address hash160(hash256(publicKey))
        private byte recipient[];
        // value of the transfer
        private long value;
        // maximum fee that sender is willing to pay
        private long fee;
        // a recoverable ec signature
        private RecoverableSignature signature;

        public BasicTransaction(byte recipient[], long value, long fee) {
            this.recipient  = recipient;
            this.value      = value;
            this.fee        = fee;
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
        public byte[] getPayload() {
            return new byte[0];
        }

        @Override
        public boolean verify() {
            // a transfer of 0 with a fee of 0 is not allowed
            return (getTransactionValue() + getTransactionFee()) != 0;
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
            return Context.getInstance().getSerialFactory().getSerialNumber(BasicTransaction.class);
        }
    }

    // this is a basic payload transaction
    // min size: 1 + 67 + (varint) + payload
    // avg size: 1 + 77 + (varint) + payload
    // max size: 1 + 81 + (varint) + payload
    public static final class PayloadTransaction extends Transaction {
        // value of the transfer
        private long value;
        // maximum fee that sender is willing to pay
        private long fee;
        // a recoverable ec signature
        private RecoverableSignature signature;
        // a valid mocha payload
        private byte payload[];

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
        public byte[] getPayload() {
            return new byte[0];
        }

        @Override
        public boolean verify() {
            return false;
        }

        @Override
        public Address getSender() throws WolkenException {
            return null;
        }

        @Override
        public Address getRecipient() {
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
            return 0;
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
        public byte[] getPayload() {
            return new byte[0];
        }

        @Override
        public boolean verify() {
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
        public int compareTo(Transaction transaction) {
            return 0;
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
            return 0;
        }
    }
}
