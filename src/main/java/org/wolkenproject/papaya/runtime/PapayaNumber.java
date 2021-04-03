package org.wolkenproject.papaya.runtime;

import java.math.BigInteger;

public class PapayaNumber extends PapayaObject {
    private final BigInteger number;

    public PapayaNumber(long number) {
        this(new BigInteger(Long.toString(number)));
    }

    public PapayaNumber(BigInteger number) {
        this.number = number;
    }

    @Override
    public BigInteger asInt() {
        return number;
    }
}
