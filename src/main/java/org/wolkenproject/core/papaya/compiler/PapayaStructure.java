package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.core.Context;
import org.wolkenproject.core.papaya.AccessModifier;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

public class PapayaStructure extends SerializableI {
    public static final int                     Alignment = 32;
    private final byte                          identifier[];
    private final StructureType                 structureType;
    private final String                        name;
    private final Map<String, PapayaField>      fieldMap;
    private final Map<String, PapayaFunction>   functionMap;
    private final LineInfo                      lineInfo;
    private long                                structureLength;

    public PapayaStructure(String name, StructureType structureType, LineInfo lineInfo) {
        this.name           = name;
        this.structureType  = structureType;
        this.fieldMap       = new LinkedHashMap<>();
        this.functionMap    = new LinkedHashMap<>();
        this.lineInfo       = lineInfo;
        this.identifier     = new byte[20];
    }

    public void addField(String name, PapayaField field) throws WolkenException {
        if (fieldMap.containsKey(name)) {
            throw new WolkenException("redeclaration of field '" + name + "' "+field.getLineInfo()+".");
        }

        field.setPosition(fieldMap.size());
        fieldMap.put(name, field);
    }

    public void addFunction(String name, PapayaFunction function) throws WolkenException {
        if (fieldMap.containsKey(name)) {
            throw new WolkenException("redeclaration of function '" + name + "' "+function.getLineInfo()+".");
        }

        functionMap.put(name, function);
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public void compile(CompiledScript script) throws WolkenException {
    }

    public void checkWriteAccess(int memberId, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        AccessModifier modifier = getMember(memberId).getAccessModifier();
        if (modifier == AccessModifier.ReadOnly) {
            throw new PapayaIllegalAccessException();
        }

        if (!Arrays.equals(stackTrace.peek().getIdentifier(), getIdentifier())) {
            switch (modifier) {
                case PrivateAccess:
                    throw new PapayaIllegalAccessException();
                case ProtectedAccess:
                    if (!stackTrace.peek().isChildOf(this)) {
                        throw new PapayaIllegalAccessException();
                    }
                    break;
            }
        }
    }

    public void checkReadAccess(int memberId, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        AccessModifier modifier = getMember(memberId).getAccessModifier();
        if (modifier == AccessModifier.ReadOnly || modifier == AccessModifier.PublicAccess) {
            return;
        }

        if (!Arrays.equals(stackTrace.peek().getIdentifier(), getIdentifier())) {
            switch (modifier) {
                case PrivateAccess:
                    throw new PapayaIllegalAccessException();
                case ProtectedAccess:
                    if (!stackTrace.peek().isChildOf(this)) {
                        throw new PapayaIllegalAccessException();
                    }
                    break;
            }
        }
    }

    public boolean isChildOf(PapayaStructure parent) {
        if (parent != null) {
            if (Arrays.equals(parent.getIdentifier(), getIdentifier())) {
                return true;
            }

            return parent.isChildOf(parent);
        }

        return false;
    }

    private PapayaMember getMember(int memberId) {
        return null;
    }

    public final byte[] getIdentifier() {
        return identifier;
    }

    public int getLength(PapayaApplication application) throws WolkenException {
        int length = 0;

        for (PapayaField field : fieldMap.values()) {
            int paddedLength = length;

            if (length % Alignment != 0) {
                paddedLength = (length / Alignment + 1) * Alignment;
            }

            int add = 0;

            switch (field.getTypeName()) {
                case "ud256":
                case "dec":
                case "dec256":
                case "int":
                case "uint":
                    add = 32;
                    break;
                case "int8":
                case "uint8":
                case "ud8":
                case "dec8":
                    add = 1;
                    break;
                case "int16":
                case "uint16":
                case "ud16":
                case "dec16":
                    add = 2;
                    break;
                case "int24":
                case "uint24":
                case "ud24":
                case "dec24":
                    add = 3;
                    break;
                case "int32":
                case "uint32":
                case "ud32":
                case "dec32":
                    add = 4;
                    break;
                case "int64":
                case "uint64":
                case "ud64":
                case "dec64":
                    add = 8;
                    break;
                case "int128":
                case "uint128":
                case "ud128":
                case "dec128":
                    add = 16;
                    break;
                default:
                    add = application.getStructureLength(field.getTypeName(), field.getLineInfo());
                    break;
            }

            length += add;
        }

        return length;
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
        return Context.getInstance().getSerialFactory().getSerialNumber(PapayaStructure.class);
    }
}
