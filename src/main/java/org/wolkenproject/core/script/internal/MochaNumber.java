package org.wolkenproject.core.script.internal;

import org.wolkenproject.exceptions.MochaException;

import java.math.BigInteger;

public class MochaNumber extends MochaObject {
    public static final BigInteger MaxSignedInt     = BigInteger.TWO.pow(255).subtract(BigInteger.ONE);
    public static final BigInteger MaxUnsignedInt   = BigInteger.TWO.pow(256).subtract(BigInteger.ONE);
    protected BigInteger    value;
    private boolean         sign; // 0 = unsigned & 1 = signed

    public MochaNumber(long integer, boolean signed) throws MochaException {
        this(new BigInteger(Long.toString(integer)), integer >= 0 && signed);
    }

    public MochaNumber(BigInteger integer, boolean signed) throws MochaException {
        this.value  = integer;
        this.sign   = signed;
        if (integer.bitLength() > 256) {
            // overflow
            value = BigInteger.ZERO;
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

    public MochaObject do_add(MochaNumber other) throws MochaException {
        BigInteger result   = value.add(other.value);

        return new MochaNumber(result, sign || other.sign);
    }

    public MochaObject do_sub(MochaNumber other) throws MochaException {
        BigInteger result   = value.subtract(other.value);
        boolean nSign       = sign || other.sign;

        // overflow
        if (value.signum() < 0 && !nSign) {
            return new MochaNumber(MaxUnsignedInt, false);
        }

        return new MochaNumber(result, nSign);
    }

    public MochaObject do_mul(MochaNumber other) throws MochaException {
        BigInteger result   = value.multiply(other.value);
        boolean nSign       = sign || other.sign;

        // overflow
        if (value.signum() < 0 && !nSign) {
            return new MochaNumber(MaxUnsignedInt, false);
        }

        return new MochaNumber(result, nSign);
    }

    @Override
    public MochaObject add(MochaObject other) throws MochaException {
        if (other instanceof MochaNumber) {
        }

        throw new MochaException("cannot perform 'add' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject sub(MochaObject other) throws MochaException {
        if (other instanceof MochaNumber) {
        }

        throw new MochaException("cannot perform 'sub' on object of type 'Number' and '"+other.getClass().getName()+"' ");
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
