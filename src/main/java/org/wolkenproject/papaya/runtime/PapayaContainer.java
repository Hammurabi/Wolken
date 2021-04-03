package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;

public interface PapayaContainer {
    public PapayaObject getAtIndex(int index) throws PapayaException;
    public void setAtIndex(int index, PapayaHandler handler) throws PapayaException;
    public void append(PapayaHandler object);
}
