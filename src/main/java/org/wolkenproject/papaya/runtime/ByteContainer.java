package org.wolkenproject.papaya.runtime;

public class ByteContainer implements PapayaContainer {
    private byte bytes[];

    public ByteContainer(byte[] bytes) {
        this.bytes = bytes;
    }
}
