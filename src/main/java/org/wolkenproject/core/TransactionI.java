package org.wolkenproject.core;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;

public abstract class TransactionI extends SerializableI {
    public static int UniqueIdentifierLength = 32;

    public static TransactionI newCoinbase(int blockHeight, String s, long reward, Address addresses[]) {
        Input inputs[] = { new Input(new byte[UniqueIdentifierLength], 0, Utils.concatenate(Utils.takeApart(blockHeight), s.getBytes())) };
        TransactionI transaction = new Transaction();
    }

    public static final class Flags
    {
        public static final int
                NO_FLAGS = 0;
    }

    public abstract int getVersion();
    public abstract int getFlag();
    public abstract int getLockTime();
    public abstract long getTotalInputValue() throws WolkenException;
    public abstract long getTotalOutputValue();
    public abstract long getRemainingValue() throws WolkenException;
    public abstract long getFee() throws WolkenException;
    public abstract Input[] getInputs();
    public abstract Output[] getOutputs();
    public abstract TransactionI signWithKey(BCECPrivateKey currentPrivateKey);
    public abstract byte[] getPayload();
    public abstract TransactionI getCloneForSignature() throws IOException, WolkenException;
    public abstract TransactionI getClone() throws IOException, WolkenException;

    public abstract byte[] getTransactionID();
}
