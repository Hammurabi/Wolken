package org.wolkenproject.core.script.internal;

import org.wolkenproject.exceptions.MochaException;

import java.math.BigInteger;

public class MochaInteger extends MochaNumber {
    public MochaInteger(BigInteger integer) throws MochaException {
        super(integer);
    }
}
