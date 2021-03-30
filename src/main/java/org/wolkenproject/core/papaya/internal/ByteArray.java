package org.wolkenproject.core.papaya.internal;

import org.wolkenproject.exceptions.PapayaException;

public class ByteArray extends MochaObject {
    private byte array[];

    public ByteArray(byte array[]) {
        this.array = array;
    }

    @Override
    public MochaObject subscriptGet(int index) throws PapayaException {
        if (index >= array.length || index < 0) {
            throw new PapayaException("accessing element '" + index + "' from array of size '" + array.length + "'.");
        }

        return new MochaNumber(Byte.toUnsignedInt(array[index]), false);
    }

    @Override
    public MochaObject subscriptSet(int index, MochaObject object) throws PapayaException {
        if (index >= array.length || index < 0) {
            throw new PapayaException("accessing element '" + index + "' from array of size '" + array.length + "'.");
        }

        if (object == null) {
            array[index] = 0;
        } else {
            array[index] = (byte) object.asInt();
        }

        return this;
    }
}
