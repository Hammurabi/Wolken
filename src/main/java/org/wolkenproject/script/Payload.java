package org.wolkenproject.script;

import org.wolkenproject.exceptions.ContractException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Payload extends SerializableI {
    /* this is the "bytecode" version of the payload */
    private int version;

    public Payload(int version) {
        this.version = version;
    }

    /* this will call any function marked as "entrypoint" in payload */
    public abstract void entryPoint(Invoker invoker) throws ContractException;

    public int getVersion() {
        return version;
    }

    @Override
    public final void write(OutputStream stream) throws IOException, WolkenException {
        VarInt.writeCompactUInt32(version, false, stream);
    }

    @Override
    public final void read(InputStream stream) throws IOException, WolkenException {
        version = VarInt.readCompactUInt32(false, stream);
    }
}
