package org.wolkenproject.papaya.intermediate;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.papaya.runtime.DefaultHandler;
import org.wolkenproject.papaya.runtime.PapayaNumber;
import org.wolkenproject.papaya.runtime.Scope;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class PushInt256 implements Opcode {
    private BigInteger    value;

    public PushInt256(BigInteger value) {
        this.value      = value;
    }

    @Override
    public void execute(Scope scope) throws PapayaException {
        scope.getStack().push(new DefaultHandler(new PapayaNumber(value, true)));
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        byte value[] = new byte[32];
        stream.read(value);
        this.value = new BigInteger(value);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
    }
}
