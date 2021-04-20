package org.wolkenproject.script;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.ContractException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

public class PapayaPayload extends Payload {
    public PapayaPayload(int version) {
        super(version);
    }

    @Override
    public void entryPoint(Invoker invoker) throws ContractException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new PapayaPayload(getVersion());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(PapayaPayload.class);
    }
}
