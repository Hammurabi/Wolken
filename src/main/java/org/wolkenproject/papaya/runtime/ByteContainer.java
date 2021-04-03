package org.wolkenproject.papaya.runtime;

public class ByteContainer implements PapayaContainer {
    private byte bytes[];

    public ByteContainer(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public PapayaObject getAtIndex(int index) {
        return null;
    }

    @Override
    public void setAtIndex(int index, PapayaHandler handler) {

    }

    @Override
    public void append(PapayaHandler object) {

    }
}
