package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;

import java.util.Arrays;

public class ByteContainer implements PapayaContainer {
    private byte bytes[];

    public ByteContainer(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public PapayaObject getAtIndex(int index) throws PapayaException {
        if (index >= bytes.length) {
            throw new PapayaException("array index out of '" + bytes.length + "' bounds.");
        }

        return new PapayaNumber(bytes[index]);
    }

    @Override
    public void setAtIndex(int index, PapayaHandler handler) throws PapayaException {
        if (index >= bytes.length) {
            throw new PapayaException("array index out of '" + bytes.length + "' bounds.");
        }

        bytes[index] = handler.getPapayaObject().asInt().byteValue();
    }

    @Override
    public void append(PapayaHandler object) {
        bytes = Arrays.copyOf(bytes, bytes.length + 1);
        bytes[bytes.length - 1] = object.asInt().byteValue();
    }
}
