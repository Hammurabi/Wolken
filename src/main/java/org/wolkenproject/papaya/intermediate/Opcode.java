package org.wolkenproject.papaya.intermediate;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.papaya.runtime.Scope;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Opcode {
    void execute(Scope scope) throws PapayaException;
    default void read(InputStream stream) throws IOException, WolkenException {}
    default void write(OutputStream stream) throws IOException, WolkenException {}
}
