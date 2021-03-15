package org.wolkenproject.core;

import org.wolkenproject.core.script.Script;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract class Transaction extends SerializableI implements Comparable<Transaction> {
    public static int UniqueIdentifierLength = 32;
    // can be represented by 1 - 4 bytes
    // version = 1 skips flags all-together

    // this should not be treated as a network version
    // transaction versions should be treated as VARINT
    // magic numbers that hint at the internal transaction
    // structure.
    private int version;
    // anything below here is optional

    // can be represented by 1 or more bytes
    // there are not enough flags at the moment
    // therefore it's represented by an int in
    // this version.
    private int flags;
    // must be 20 bytes
    private byte recipient[];
    // value
    private long value;
    // maximum fee that sender is willing to pay
    private long fee;
    // a recoverable ec signature
    private RecoverableSignature signature;
    // a mocha payload
    private byte payload[];

    @Override
    public int compareTo(Transaction transaction) {
        return 0;
    }

    private void writeBasic(OutputStream stream) throws IOException, WolkenException {
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        VarInt.writeCompactUInt32(version, false, stream);
        if (version == Magic.BasicTransaction) {
            stream.write(recipient);
            VarInt.writeCompactUInt32(value, false, stream);
            VarInt.writeCompactUInt32(fee, false, stream);
            stream.write(signature.getV());
            stream.write(signature.getR());
            stream.write(signature.getS());
        } else if (version == Magic.BasicTransactionToAlias) {
            stream.write(recipient);
            VarInt.writeCompactUInt32(value, false, stream);
            VarInt.writeCompactUInt32(fee, false, stream);
            stream.write(signature.getV());
            stream.write(signature.getR());
            stream.write(signature.getS());
        } else if (version == Magic.BasicFlaggedTransaction) {
            // write it as a single byte
            // we only have up to 8 flags
            // at the moment.
            stream.write(flags & 0xFF);

            if (hasFlag(Flags.TwoByteFlags)) {
                stream.write((flags >> 8) & 0xFF);
            }

            // write the payload
            if (hasFlag(Flags.MochaPayload)) {
                VarInt.writeCompactUInt32(payload.length, false, stream);
                stream.write(payload);
            }

            VarInt.writeCompactUInt32(fee, false, stream);
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        version = VarInt.readCompactUInt32(false, stream);

        if (version == Magic.BasicTransaction) {
        } else if (version == Magic.BasicFlaggedTransaction) {
            flags = stream.read();

            if (hasFlag(Flags.TwoByteFlags)) {
                flags |= stream.read() << 8;
            }

            // read the payload
            if (hasFlag(Flags.MochaPayload)) {
                int length = VarInt.readCompactUInt32(false, stream);
                payload     = new byte[length];
                stream.read(payload);
            }
        }
    }

    public boolean hasFlag(int flag) {
        return (flags & flag) == flag;
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Transaction();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Transaction.class);
    }

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

//        public static boolean hasLocktime(int flags)
//        {
//            return (flags & RelativeLockTime) == RelativeLockTime || (flags & TimestampLockTime) == TimestampLockTime;
//        }
    }

    public int getVersion() {
        return version;
    }

    public int getFlags() {
        return flags;
    }

    public long getTransactionValue() {
        return transactionContent.getTransactionValue();
    }

    public long getTransactionFee() {
        return transactionContent.getTransactionFee();
    }

    public byte[] getPayload() {
        return transactionContent.getPayload();
    }

    public byte[] getTransactionID() {
        return HashUtil.sha256d(asByteArray());
    }

    public boolean verify() {
        return false;
    }

    public Address getSender() {
        return null;
    }

    public Address getRecipient() {
        return null;
    }

    // multiple recipients and senders might be possible in the future
    public Address[] getSenders() {
        return new Address[] { getSender() };
    }
    public Address[] getRecipients() {
        return new Address[] { getRecipient() };
    }


    // a transaction that creates a contract
    public static Transaction newPayload(long amount, byte payLoad[]) {
        return new Transaction();
    }

    // a purely monetary transaction
    public static Transaction newTransfer(byte recipient[], long amount, long fee) {
        return new Transaction();
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

        return new org.wolkenproject.core.transactions.Transaction(
                Context.getInstance().getNetworkParameters().getVersion(),
                Flags.RelativeLockTime,
                Context.getInstance().getNetworkParameters().getCoinbaseLockTime(),
                inputs,
                outputs);
    }

    public static final void register() {
    }
}
