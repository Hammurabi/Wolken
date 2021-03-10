package org.wolkenproject.core.script.internal;

import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;

public class MochaInt extends MochaObject {
    private BigInteger value;

    public MochaInt(long value, boolean signed) {
        this.value = new BigInteger(signed ? 0 : 1, Utils.takeApartLong(value));
    }

    public MochaInt(BigInteger integer) throws MochaException {
        this.value = integer;
        if (integer.bitLength() > 256) {
            throw new MochaException("the maximum size of int is 256 bits.");
        }
    }

    @Override
    public MochaObject add(MochaObject other) {
        if (other instanceof MochaInt) {
            return new MochaInt(value.add(((MochaInt) other).value));
        }

        return this;
    }
}
