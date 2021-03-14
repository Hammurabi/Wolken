package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Account extends SerializableI {
    private final long nonce;
    private final long balance;

    public Account(long nonce, long balance) {
        this.nonce      = nonce;
        this.balance    = balance;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        // in the case of nonce 2^61-1 should suffice for now
        VarInt.writeCompactUInt64(nonce, false, stream);
        // as for balance, coincidentally/fortunately, the maximum
        // number of coins to be issues matches 61 bits exactly
        // and so for now, it's safe to drop the last 3 bits.
        VarInt.writeCompactUInt64(balance, false, stream);
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
        return Context.getInstance().getSerialFactory().getSerialNumber(Account.class);
    }

    public long getNonce() {
        return nonce;
    }

    public long getBalance() {
        return balance;
    }
}
