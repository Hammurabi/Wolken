package org.wokenproject.core;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;

public abstract class TransactionI {
    public static int UniqueIdentifierLength = 32;

    public static final class Flags
    {
        public static final int
                NO_FLAGS = 0;
    }

    public abstract int getVersion();
    public abstract int getFlag();
    public abstract int getLockTime();
    public abstract long getTotalValue();
    public abstract long getRemainingValue();
    public abstract long getFee();
    public abstract Input[] getInputs();
    public abstract Output[] getOutputs();
    public abstract TransactionI signWithKey(BCECPrivateKey currentPrivateKey);
    public abstract byte[] getPayload();
    public abstract TransactionI getCloneForSignature();
    public abstract TransactionI getClone();
}
