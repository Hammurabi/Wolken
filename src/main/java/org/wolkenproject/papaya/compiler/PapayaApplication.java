package org.wolkenproject.papaya.compiler;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.ContractException;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.script.Invoker;
import org.wolkenproject.script.Payload;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.ByteArray;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PapayaApplication extends Payload {
    // this contains structures in order of declaration
    private Map<ByteArray, PapayaStructure> structureMap;
    private int                             version;
    private BigInteger                      flags;

    public PapayaApplication(int version) {
        this.structureMap   = new LinkedHashMap<>();
        this.version        = version;
    }

    public void addStructure(ByteArray name, PapayaStructure structure) throws PapayaException {
        if (structureMap.containsKey(name)) {
            throw new PapayaException("redeclaration of structure '" + name + "' {"+structure.getLineInfo()+"}.");
        }

        structureMap.put(name, structure);
    }

    public void compile() throws WolkenException {
        for (PapayaStructure structure : structureMap.values()) {
            structure.compile(this);
        }
    }

//    public int getStructureLength(String name, LineInfo lineInfo) throws WolkenException {
//        if (!structureMap.containsKey(name)) {
//            throw new WolkenException("reference to undefined type '" + name + "' at " + lineInfo + ".");
//        }
//
//        return structureMap.get(name).getLength(this);
//    }

    @Override
    public void entryPoint(Invoker invoker) throws ContractException {
    }

    @Override
    public int getVersion() {
        return version;
    }

    public void writePayload(OutputStream stream) throws IOException, WolkenException {
        // write a header that contains informations about the application
        //TODO: find a better way to write the flags automagically.
        VarInt.writeCompactFlags(flags, stream);

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

    @Override
    public void readPayload(InputStream stream) throws IOException, WolkenException {
        version = VarInt.readCompactUInt32(false, stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new PapayaApplication(version);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(PapayaApplication.class);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (PapayaStructure structure : structureMap.values()) {
            builder.append(structure).append("\n------------------------------------------------\n");
        }

        return builder.toString();
    }

    public static final class Flags {
        public static final long
                None = 0x0,
                IncludeLineInfo = 1,
                /* if this flag is set, the compression engine will find the best compression scheme for ascii ids */
                ASCIINames      = 1<<2,
                /* if this flag is set, the compression engine will find the best compression scheme for utf-16 ids but will require a language enum */
                UTFNames        = 1<<3,
                x16BitFlags     = 1<<8,
                x24BitFlags     = 1<<16,
                x32BitFlags     = 1<<24,
                x40BitFlags     = 1<<32,
                x48BitFlags     = 1<<40,
                x56BitFlags     = 1<<48,
                x64BitFlags     = 1<<56;

        public static long setFlag(long flags, long newFlag) {
            return flags | newFlag;
        }

        public static boolean isFlagSet(long flags, long flag) {
            return (flags & flag) == flag;
        }
    }
}
