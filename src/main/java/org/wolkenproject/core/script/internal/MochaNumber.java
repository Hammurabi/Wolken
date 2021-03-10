package org.wolkenproject.core.script.internal;

import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;

public class MochaNumber extends MochaObject {
    private BigInteger value;

    public MochaNumber(long value, boolean signed) {
        this.value = new BigInteger(signed ? 0 : 1, Utils.takeApartLong(value));
    }

    public MochaNumber(BigInteger integer) throws MochaException {
        this.value = integer;
        if (integer.bitLength() > 256) {
            throw new MochaException("the maximum size of int is 256 bits.");
        }
    }

    @Override
    public MochaObject add(MochaObject other) throws MochaException {
        if (other instanceof MochaNumber) {
            return new MochaNumber(value.add(((MochaNumber) other).value));
        }

        throw new MochaException("cannot perform 'add' on object of type 'int' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject sub(MochaObject other) throws MochaException {
        if (other instanceof MochaNumber) {
            return new MochaNumber(value.subtract(((MochaNumber) other).value));
        }

        throw new MochaException("cannot perform 'sub' on object of type 'int' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject mul(MochaObject other) throws MochaException {
        if (other instanceof MochaNumber) {
            return new MochaNumber(value.multiply(((MochaNumber) other).value));
        }

        throw new MochaException("cannot perform 'mul' on object of type 'int' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject div(MochaObject other, boolean sign) throws MochaException {
        if (other instanceof MochaNumber) {
            return new MochaNumber(value.divide(((MochaNumber) other).value));
        }

        throw new MochaException("cannot perform 'div' on object of type 'int' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject mod(MochaObject other, boolean sign) throws MochaException {
        if (other instanceof MochaNumber) {
            return new MochaNumber(value.mod(((MochaNumber) other).value));
        }

        throw new MochaException("cannot perform 'mod' on object of type 'int' and '"+other.getClass().getName()+"' ");
    }
}
