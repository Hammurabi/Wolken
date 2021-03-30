package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class PapayaApplication extends SerializableI {
    // this contains structures in order of declaration
    private final Map<String, PapayaStructure>  structureMap;
    private final int                           version;

    public PapayaApplication() {
        this.structureMap   = new LinkedHashMap<>();
        this.version        = 1;
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
        // write a header that contains informations about the application.

        // we first write a compacted uint29 containing the bytecode version.
        VarInt.writeCompactUInt32(version, false, stream);
        // write the amount of structures.
        VarInt.writeCompactUInt32(structureMap.size(), false, stream);
        // write all the structures into the stream.
        for (PapayaStructure structure : structureMap.values()) {
            byte name[] = structure.getName().getBytes();

            // write the structure name.
            VarInt.writeCompactUInt32(name.length, false, stream);
            stream.write(name);

            // write the structure type.
            StructureType.write(structure.getStructureType(), stream);

            // write all the structure fields.
            for (PapayaField field : structure.getFieldMap().values()) {
                byte fieldName[] = field.getName().getBytes();
                byte fieldType[] = field.getTypeName().getBytes();

                // write the field name.
                VarInt.writeCompactUInt32(fieldName.length, false, stream);
                stream.write(fieldName);

                // write the field type.
                VarInt.writeCompactUInt32(fieldType.length, false, stream);
                stream.write(fieldType);
            }
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new PapayaApplication();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(PapayaApplication.class);
    }
}
