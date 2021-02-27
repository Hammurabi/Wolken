package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Input {
    private byte    previousTXID[];
    private int     index;
    private byte    payload[];

    public Input(byte previousTXID[], int index, byte payload[]) {
        this.previousTXID   = previousTXID;
        this.index          = index;
        this.payload        = payload;
    }

    public Input(DataInputStream stream) throws IOException {
        previousTXID = new byte[32];
        stream.read(previousTXID);
        index = (int) stream.readChar();
        payload = new byte[stream.readInt()];
        stream.read(payload);
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

    public void write(DataOutputStream stream) throws IOException {
        stream.write(previousTXID);
        stream.writeChar(index);
        stream.writeInt(payload.length);
        stream.write(payload);
    }
}
