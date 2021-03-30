package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.core.Context;
import org.wolkenproject.core.papaya.AccessModifier;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PapayaApplication {
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

    public void write(OutputStream stream) throws IOException, WolkenException {
        // write a header that contains informations about the application.

        // we first write a compacted uint29 containing the bytecode version.
        VarInt.writeCompactUInt32(version, false, stream);
        // write the amount of structures.
        VarInt.writeCompactUInt32(structureMap.size(), false, stream);

        // write all the structures into the stream.
        for (PapayaStructure structure : structureMap.values()) {
            // write the structure identifier.
            VarInt.writeCompactUint128(structure.getIdentifierInt(), false, stream);

            // write the structure type.
            StructureType.write(structure.getStructureType(), stream);

            Set<PapayaMember> members       = structure.getMembers();
            Set<PapayaFunction> functions   = structure.getFunctions();

            //write the number of members to expect.
            VarInt.writeCompactUInt32(members.size(), false, stream);
            //write the number of functions to expect.
            VarInt.writeCompactUInt32(functions.size(), false, stream);

            // write all the structure fields.
            for (PapayaMember member : members) {
                // write the identifier to the stream.
                VarInt.writeCompactUint128(member.getIdentifierInt(), false, stream);
                // write the access modifier to the stream.
                AccessModifier.write(member.getAccessModifier(), stream);
            }

            // write the function opcodes.
            for (PapayaFunction function : functions) {
                // get the function's bytecode.
                byte byteCode[] = function.getByteCode();

                // write the bytecode length.
                VarInt.writeCompactUInt32(byteCode.length, false, stream);

                // write the bytecode.
                stream.write(byteCode);
            }
        }
    }
}
