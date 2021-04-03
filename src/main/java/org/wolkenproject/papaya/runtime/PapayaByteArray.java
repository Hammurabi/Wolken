package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;

public class PapayaByteArray extends PapayaObject {
    private PapayaContainer container;

    public PapayaByteArray(byte[] bytes) {
        super();
        this.container = new ByteContainer(bytes);
    }

    @Override
    public PapayaContainer asContainer() throws PapayaException {
        return container;
    }
}
