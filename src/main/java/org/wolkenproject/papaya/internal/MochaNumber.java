package org.wolkenproject.papaya.internal;

import org.wolkenproject.exceptions.PapayaException;

import java.math.BigInteger;

public class MochaNumber extends MochaObject {
    public static final BigInteger MaxSignedInt     = new BigInteger("2").pow(255).subtract(BigInteger.ONE);
    public static final BigInteger MaxUnsignedInt   = new BigInteger("2").pow(256).subtract(BigInteger.ONE);
    protected BigInteger    value;
    private boolean         sign; // 0 = unsigned & 1 = signed

    public MochaNumber(String integer, boolean signed) {
        this(new BigInteger(integer), signed);
    }

    public MochaNumber(long integer, boolean signed) {
        this(new BigInteger(Long.toString(integer)), integer >= 0 && signed);
    }

    public MochaNumber(BigInteger integer, boolean signed) {
        this.value  = integer;
        this.sign   = signed;
        if (integer.bitLength() > 256) {
            // overflow
            value = BigInteger.ZERO;
        }
    }

    @Override
    public boolean isTrue() {
        return value.compareTo(BigInteger.ZERO) != 0;
    }

    public MochaObject do_add(MochaNumber other) throws PapayaException {
        BigInteger result   = value.add(other.value);

        return new MochaNumber(result, sign || other.sign);
    }

    public MochaObject do_sub(MochaNumber other) throws PapayaException {
        BigInteger result   = value.subtract(other.value);
        boolean nSign       = sign || other.sign;

        // overflow
        if (value.signum() < 0 && !nSign) {
            return new MochaNumber(MaxUnsignedInt, false);
        }

        return new MochaNumber(result, nSign);
    }

    public MochaObject do_mul(MochaNumber other) throws PapayaException {
        BigInteger result   = value.multiply(other.value);
        boolean nSign       = sign || other.sign;

        // overflow
        if (value.signum() < 0 && !nSign) {
            return new MochaNumber(MaxUnsignedInt, false);
        }

        return new MochaNumber(result, nSign);
    }

    public MochaObject do_div(MochaNumber other) throws PapayaException {
        BigInteger result   = value.divide(other.value);
        boolean nSign       = sign || other.sign;

        // overflow
        if (value.signum() < 0 && !nSign) {
            return new MochaNumber(MaxUnsignedInt, false);
        }

        return new MochaNumber(result, nSign);
    }

    public MochaObject do_mod(MochaNumber other) throws PapayaException {
        BigInteger result   = value.mod(other.value);
        boolean nSign       = sign || other.sign;

        // overflow
        if (value.signum() < 0 && !nSign) {
            return new MochaNumber(MaxUnsignedInt, false);
        }

        return new MochaNumber(result, nSign);
    }

    public MochaObject do_shiftRight(MochaNumber other) throws PapayaException {
        BigInteger result   = value.shiftRight(other.value.intValue());
        boolean nSign       = sign || other.sign;

        // overflow
        if (value.signum() < 0 && !nSign) {
            return new MochaNumber(MaxUnsignedInt, false);
        }

        return new MochaNumber(result, nSign);
    }

    public MochaObject do_shiftLeft(MochaNumber other) throws PapayaException {
        BigInteger result   = value.shiftLeft(other.value.intValue());
        boolean nSign       = sign || other.sign;

        // overflow
        if (value.signum() < 0 && !nSign) {
            return new MochaNumber(MaxUnsignedInt, false);
        }

        return new MochaNumber(result, nSign);
    }

    @Override
    public MochaObject add(MochaObject other) throws PapayaException {
        if (other instanceof MochaNumber) {
            do_add((MochaNumber) other);
        }

        throw new PapayaException("cannot perform 'add' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject sub(MochaObject other) throws PapayaException {
        if (other instanceof MochaNumber) {
            do_sub((MochaNumber) other);
        }

        throw new PapayaException("cannot perform 'sub' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject mul(MochaObject other) throws PapayaException {
        if (other instanceof MochaNumber) {
            do_mul((MochaNumber) other);
        }

        throw new PapayaException("cannot perform 'mul' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject div(MochaObject other) throws PapayaException {
        if (other instanceof MochaNumber) {
            do_div((MochaNumber) other);
        }

        throw new PapayaException("cannot perform 'div' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }

    @Override
    public MochaObject mod(MochaObject other) throws PapayaException {
        if (other instanceof MochaNumber) {
            do_mod((MochaNumber) other);
        }

        throw new PapayaException("cannot perform 'mod' on object of type 'Number' and '"+other.getClass().getName()+"' ");
    }
}
