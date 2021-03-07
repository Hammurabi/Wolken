package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OpcodeList extends SerializableI {
    // this class should be used to store opcodes
    // it will compact them in the most optimal
    // way for storage/network transfers to help
    // reduce the cost of transactions.

    // contains bits from arguments
    private long argumentBits;

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
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
        return 0;
    }
}
