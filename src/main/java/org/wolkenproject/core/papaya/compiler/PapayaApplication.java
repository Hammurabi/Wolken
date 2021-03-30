package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class PapayaApplication extends SerializableI {
    // this contains structures in order of declaration
    private final Map<String, PapayaStructure> structureMap;

    public PapayaApplication() {
        this.structureMap = new LinkedHashMap<>();
    }

    public void addStructure(String name, PapayaStructure structure) throws WolkenException {
        if (structureMap.containsKey(name)) {
            throw new WolkenException("redeclaration of structure '" + name + "' {"+structure.getLineInfo()+"}.");
        }

        structureMap.put(name, structure);
    }

    public CompiledScript compile() throws WolkenException {
        CompiledScript compiledScript = new CompiledScript(this);

        for (PapayaStructure structure : structureMap.values()) {
            structure.compile(compiledScript);
        }

        return compiledScript;
    }

    public int getStructureLength(String name, LineInfo lineInfo) throws WolkenException {
        if (!structureMap.containsKey(name)) {
            throw new WolkenException("reference to undefined type '" + name + "' at " + lineInfo + ".");
        }

        return structureMap.get(name).getLength(this);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return null;
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(PapayaApplication.class);
    }
}
