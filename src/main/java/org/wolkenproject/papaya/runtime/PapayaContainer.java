package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;

import java.math.BigInteger;

public interface PapayaContainer {
    public PapayaObject getAtIndex(int index) throws PapayaException;
    public void setAtIndex(int index, PapayaHandler handler) throws PapayaException;
    public void append(PapayaHandler object);
    public BigInteger asInt();
    public BigInteger asSignedInt();
}
