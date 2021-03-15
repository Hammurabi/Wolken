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

    public int getVersion() {
        return getSerialNumber();
    }

    public abstract int getFlags();
    public abstract long getTransactionValue();
    public abstract long getTransactionFee();
    public abstract byte[] getPayload();
    public abstract boolean verify();
    public abstract Address getSender();
    public abstract Address getRecipient();

    // multiple recipients and senders might be possible in the future
    public Address[] getSenders() {
        return new Address[] { getSender() };
    }
    public Address[] getRecipients() {
        return new Address[] { getRecipient() };
    }

    public byte[] getTransactionID() {
        return HashUtil.sha256d(asByteArray());
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

    public static final void register(SerializationFactory factory) {
        factory.registerClass(BasicTransaction.class, new BasicTransaction());
        factory.registerClass(FlaggedTransaction.class, new FlaggedTransaction());
    }

    public static final class BasicTransaction extends Transaction {
    }

    public static final class FlaggedTransaction extends Transaction {
    }
}
