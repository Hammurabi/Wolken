package org.wolkenproject.core.script.internal;

import org.wolkenproject.exceptions.MochaException;

import java.math.BigInteger;

public class MochaNumber extends MochaObject {
    public static final BigInteger MaxSignedInt     = BigInteger.TWO.pow(255).subtract(BigInteger.ONE);
    public static final BigInteger MaxUnsignedInt   = BigInteger.TWO.pow(256).subtract(BigInteger.ONE);
    protected BigInteger value;

    public MochaNumber(BigInteger integer) throws MochaException {
        this.value = integer;

        if (integer.bitLength() > 256) {
            throw new MochaException("The maximum size of Number is 256 bits.");
        }
    }

    protected BigInteger fixOverflow(BigInteger integer, boolean signed) {
        BigInteger result = integer;
        BigInteger subint = signed ? MaxSignedInt : MaxUnsignedInt;
        while (result.bitLength() > 256) {
            result = integer.subtract(subint);
        }

        return result;
    }

    @Override
    public MochaObject add(MochaObject other) throws MochaException {
        if (other instanceof MochaNumber) {
            cAdd(other);
        }

        throw new MochaException("cannot perform 'add' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }

    private void cAdd(MochaObject other) {
    }

    @Override
    public MochaObject sub(MochaObject other) throws MochaException {
        if (other instanceof MochaNumber) {
            cSub(other);
        }

        throw new MochaException("cannot perform 'sub' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }

    private void cSub(MochaObject other) {
    }

    @Override
    public MochaObject mul(MochaObject other) throws MochaException {
        if (other instanceof MochaNumber) {
        }

        throw new MochaException("cannot perform 'mul' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject div(MochaObject other, boolean sign) throws MochaException {
        if (other instanceof MochaNumber) {
        }

        throw new MochaException("cannot perform 'div' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject mod(MochaObject other, boolean sign) throws MochaException {
        if (other instanceof MochaNumber) {
        }

        throw new MochaException("cannot perform 'mod' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }
}
