package org.wolkenproject.core;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.wolkenproject.core.script.Script;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Transaction extends SerializableI implements Comparable<Transaction> {
    public static int UniqueIdentifierLength = 32;
    // can be represented by 1 - 4 bytes
    // version = 1 skips flags all-together
    private int version;
    // anything below here is optional

    // can be represented by 1 or more bytes
    // there are not enough flags at the moment
    // therefore it's represented by an int in
    // this version.
    private int flags;

    // can be more than one recipient if FLAG&MULTIPLE_RECIPIENTS==MULTIPLE_RECIPIENTS
    // must be 20 bytes
    private byte recipient[];

    // value
    private long value;

    // value
    private long fee;

    // signature data
    private byte v;
    private byte r[];
    private byte s[];

    // payload to execute
    private byte payload[];


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
        return value;
    }

    public long getFee() {
        return fee;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] getTransactionID() {
        return HashUtil.sha256d(asByteArray());
    }

    public boolean verify() {
        return false;
    }
}
