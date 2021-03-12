package org.wolkenproject.core.script.internal;

import org.wolkenproject.exceptions.MochaException;

public class ByteArray extends MochaObject {
    private byte array[];

    public ByteArray(byte array[]) {
        this.array = array;
    }

    @Override
    public MochaObject subscriptGet(int index) throws MochaException {
        if (index >= array.length || index < 0) {
            throw new MochaException("accessing element '" + index + "' from array of size '" + array.length + "'.");
        }

        return new MochaNumber(array[index]);
    }

    @Override
    public MochaObject subscriptSet(int index, MochaObject object) throws MochaException {
        if (index >= array.length || index < 0) {
            throw new MochaException("accessing element '" + index + "' from array of size '" + array.length + "'.");
        }

        if (object == null) {
            array[index] = 0;
        } else {
            array[index] = (byte) object.asInt();
        }

        return this;
    }
}
