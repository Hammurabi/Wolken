package org.wolkenproject.papaya.compiler;

import org.wolkenproject.papaya.AccessModifier;
import org.wolkenproject.papaya.PapayaObject;
import org.wolkenproject.papaya.PapayaReadOnlyWrapper;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.exceptions.WolkenException;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.*;

public class PapayaStructure {
    public static final int                     Alignment = 32;
    private final byte                          identifier[];
    private final StructureType                 structureType;
    private final String                        name;
    private final Set<PapayaMember>             members;
    private final LineInfo                      lineInfo;
    private long                                structureLength;

    public PapayaStructure(String name, StructureType structureType, LineInfo lineInfo) {
        this.name           = name;
        this.structureType  = structureType;
        this.members        = new LinkedHashSet<>();
        this.lineInfo       = lineInfo;
        this.identifier     = new byte[20];
    }

    public void addField(String name, PapayaField field) throws PapayaException {
        if (containsMember(name)) {
            throw new PapayaException("redeclaration of field '" + name + "' "+field.getLineInfo()+".");
        }

        field.setIdentifier(members.size());
        members.add(field);
    }

    public void addFunction(String name, PapayaFunction function) throws PapayaException {
        if (containsMember(name)) {
            throw new PapayaException("redeclaration of function '" + name + "' "+function.getLineInfo()+".");
        }

        function.setIdentifier(members.size());
        members.add(function);
    }

    public void addMember(String name, PapayaMember member) throws PapayaException {
        if (containsMember(name)) {
            throw new PapayaException("redeclaration of member '" + name + "'.");
        }

        member.setIdentifier(members.size());
        members.add(member);
    }

    public boolean containsMember(String name) {
        for (PapayaMember member : members) {
            if (member.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsMemberId(byte id[]) {
        for (PapayaMember member : members) {
            if (Arrays.equals(member.getIdentifier(), id)) {
                return true;
            }
        }

        return false;
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public void compile(PapayaApplication application) throws WolkenException {
        for (PapayaFunction function : getFunctions()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CompilationScope compilationScope = new CompilationScope(outputStream, application);
            function.getStatement().compile(compilationScope);
            function.setByteCode(outputStream.toByteArray());
        }
    }

    public Set<PapayaField> getFields() {
        Set<PapayaField> functions = new LinkedHashSet<>();
        for (PapayaMember member : getMembers()) {
            if (member instanceof PapayaField) {
                functions.add((PapayaField) member);
            }
        }

        return functions;
    }

    public Set<PapayaFunction> getFunctions() {
        Set<PapayaFunction> functions = new LinkedHashSet<>();
        for (PapayaMember member : getMembers()) {
            if (member instanceof PapayaFunction) {
                functions.add((PapayaFunction) member);
            }
        }

        return functions;
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

    public PapayaObject checkMemberAccess(PapayaObject member, int memberId, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        AccessModifier modifier = getMember(memberId).getAccessModifier();
        // if it is read only, then we return a wrapper object.
        if (modifier == AccessModifier.PublicAccess) {
            return member;
        }

        if (modifier == AccessModifier.ReadOnly) {
            return new PapayaReadOnlyWrapper(member);
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

        return new PapayaReadOnlyWrapper(member);
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

        for (PapayaField field : getFields()) {
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

    public StructureType getStructureType() {
        return structureType;
    }

    public String getName() {
        return name;
    }

    public Set<PapayaMember> getMembers() {
        return members;
    }

    public BigInteger getIdentifierInt() {
        return new BigInteger(1, getIdentifier());
    }
}
