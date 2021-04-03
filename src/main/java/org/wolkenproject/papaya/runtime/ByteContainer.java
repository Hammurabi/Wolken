package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;

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
    public void setAtIndex(int index, PapayaHandler handler) {
    }

    @Override
    public void append(PapayaHandler object) {
    }
}
