package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.*;

public class Input extends SerializableI {
    private byte    previousTXID[];
    private int     index;
    private byte    payload[];

    public Input(byte previousTXID[], int index, byte payload[]) {
        this.previousTXID   = previousTXID;
        this.index          = index;
        this.payload        = payload;
    }

    public byte[] getChainLink() {
        return previousTXID;
    }

    public int getIndex() {
        return index;
    }

    public boolean getSpendable() {
        return Context.getInstance().getDatabase().getOutputExists(previousTXID, (char) index);
    }

    public Output getOutput() throws WolkenException {
        return Context.getInstance().getDatabase().findOutput(previousTXID, (char) index).getResult();
    }

    public byte[] getData() {
        return payload;
    }

    public void setData(byte[] data) {
        this.payload = data;
    }

    public void write(OutputStream stream) throws IOException {
        stream.write(previousTXID);
        Utils.writeUnsignedInt16(index, stream);
        Utils.writeUnsignedInt16(payload.length, stream);
        stream.write(payload);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        stream.read(previousTXID);
        byte buffer[] = new byte[2];
        stream.read(buffer);
        index = Utils.makeInt((byte) 0, (byte) 0, buffer[0], buffer[1]);
        stream.read(buffer);
        int length = Utils.makeInt((byte) 0, (byte) 0, buffer[0], buffer[1]);
        payload = new byte[length];
        stream.read(payload);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Input(new byte[TransactionI.UniqueIdentifierLength], 0, new byte[0]);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Input.class);
    }
}
