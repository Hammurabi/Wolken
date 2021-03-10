package org.wolkenproject.core.script.internal;

import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;

public class MochaSignedInt extends MochaNumber {
    public MochaSignedInt(long value) {
        this.value = new BigInteger(Utils.takeApartLong(value));
    }

    public MochaSignedInt(BigInteger integer) throws MochaException {
        this.value = integer;
        if (integer.bitLength() > 256) {
            throw new MochaException("the maximum size of int is 256 bits.");
        }
    }
}
