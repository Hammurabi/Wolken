package org.wolkenproject.papaya.runtime;

public interface PapayaContainer {
    public PapayaObject getAtIndex(int index);
    public void setAtIndex(int index, PapayaHandler handler);
    public void append(PapayaHandler object);
}
