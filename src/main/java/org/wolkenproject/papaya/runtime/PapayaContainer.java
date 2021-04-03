package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.AccessModifier;

public interface PapayaContainer {
    public PapayaObject getAtIndex(int index, AccessModifier modifier);
    public void setAtIndex(int index, PapayaHandler handler, AccessModifier modifier);
    public void append(PapayaHandler object, AccessModifier modifier);
}
