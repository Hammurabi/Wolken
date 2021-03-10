package org.wolkenproject.core.script.internal;

import org.wolkenproject.utils.Utils;

import java.math.BigInteger;

public class MochaInt extends MochaObject {
    private BigInteger value;

    public MochaInt(long value, boolean signed) {
        this.value = new BigInteger(signed ? 0 : 1, Utils.takeApartLong(value));
    }
}
