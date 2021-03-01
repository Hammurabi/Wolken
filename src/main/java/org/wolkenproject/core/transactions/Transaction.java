package org.wolkenproject.core.transactions;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.Input;
import org.wolkenproject.core.Output;
import org.wolkenproject.core.TransactionI;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Transaction extends TransactionI {
    private int version;
    private int flags;
    private int locktime;
    private Input inputs[];
    private Output outputs[];

    public Transaction(int version, int flags, int locktime, Input[] inputs, Output[] outputs)
    {
        this.version = version;
        this.flags = flags;
        this.locktime = locktime;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public int getFlag() {
        return flags;
    }

    @Override
    public int getLockTime() {
        return locktime;
    }

    @Override
    public long getTotalInputValue() throws WolkenException {
        long value = 0;
        for (Input input : inputs)
        {
            value += input.getOutput().getValue();
        }

        return value;
    }

    @Override
    public long getTotalOutputValue() {
        long value = 0;
        for (Output output : outputs)
        {
            value += output.getValue();
        }

        return value;
    }

    @Override
    public long getRemainingValue() throws WolkenException {
        long input  = getTotalInputValue();
        long output = getTotalOutputValue();

        return input - output;
    }

    @Override
    public long getFee() throws WolkenException {
        return getRemainingValue();
    }

    @Override
    public Input[] getInputs() {
        return inputs;
    }

    @Override
    public Output[] getOutputs() {
        return outputs;
    }

    @Override
    public TransactionI signWithKey(BCECPrivateKey currentPrivateKey) {
        return null;
    }

    @Override
    public byte[] getPayload() {
        return new byte[0];
    }

    @Override
    public TransactionI getCloneForSignature() throws IOException, WolkenException {
        return makeCopy();
    }

    @Override
    public TransactionI getClone() throws IOException, WolkenException {
        return makeCopy();
    }

    @Override
    public byte[] getTransactionID() {
        return HashUtil.sha256d(asByteArray());
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(version, stream);
        Utils.writeInt(flags, stream);
        if (Flags.hasLocktime(flags)) {
            Utils.writeInt(locktime, stream);
        }
        Utils.writeUnsignedInt16(inputs.length, stream);
        Utils.writeUnsignedInt16(outputs.length, stream);
        for (Input input : inputs) {
            input.write(stream);
        }
        for (Output output : outputs) {
            output.write(stream);
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        byte buffer[] = new byte[4];
        stream.read(buffer);
        this.version    = Utils.makeInt(buffer);
        stream.read(buffer);
        this.flags      = Utils.makeInt(buffer);
        if (Flags.hasLocktime(flags)) {
            stream.read(buffer);
            this.locktime = Utils.makeInt(buffer);
        }
        stream.read(buffer, 0, 2);
        int numInputs   = Utils.makeInt((byte) 0, (byte) 0, buffer[0], buffer[1]);
        stream.read(buffer, 0, 2);
        int numOutputs  = Utils.makeInt((byte) 0, (byte) 0, buffer[0], buffer[1]);
        this.inputs     = new Input[numInputs];
        this.outputs    = new Output[numOutputs];
        for (int i = 0; i < numInputs; i ++) {
            inputs[i]   = Context.getInstance().getSerialFactory().fromStream(Context.getInstance().getSerialFactory().getSerialNumber(Input.class), stream);
        }
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Transaction(0, 0, 0, new Input[0], new Output[0]);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Transaction.class);
    }
}
