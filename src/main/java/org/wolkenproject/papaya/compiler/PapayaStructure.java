package org.wolkenproject.papaya.compiler;

import org.wolkenproject.papaya.runtime.PapayaObject;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.PapayaIllegalAccessException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.papaya.runtime.PapayaHandler;
import org.wolkenproject.utils.ByteArray;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PapayaStructure {
    public static final int                     Alignment = 32;
    private final ByteArray                     identifier;
    private final StructureType                 structureType;
    private final Set<PapayaMember>             members;
    private final Map<Integer, ByteArray>       operators;
    private final LineInfo                      lineInfo;
    private long                                structureLength;

    public PapayaStructure(String name, StructureType structureType, LineInfo lineInfo) {
        this.structureType  = structureType;
        this.members        = new LinkedHashSet<>();
        this.operators      = new HashMap<>();
        this.lineInfo       = lineInfo;
        this.identifier     = ByteArray.wrap(name.getBytes(StandardCharsets.UTF_8));
    }

    public ByteArray getOperatorId(int operator) throws PapayaException {
        if (operators.containsKey(operator)) {
            return operators.get(operator);
        }

        throw new PapayaException("operator '"+operator+"' is not overloaded for '"+getName()+"'.");
    }

    public void addField(String name, PapayaField field) throws PapayaException {
        if (containsMember(name)) {
            throw new PapayaException("redeclaration of field '" + name + "' "+field.getLineInfo()+".");
        }

        members.add(field);
    }

    public void addFunction(String name, PapayaFunction function) throws PapayaException {
        if (containsMember(name)) {
            throw new PapayaException("redeclaration of function '" + name + "' "+function.getLineInfo()+".");
        }

        members.add(function);
    }

    public void addMember(String name, PapayaMember member) throws PapayaException {
        if (containsMember(name)) {
            throw new PapayaException("redeclaration of member '" + name + "'.");
        }

        members.add(member);
    }

    public boolean containsMember(String name) {
        for (PapayaMember member : members) {
            if (member.getIdentifier().equals(ByteArray.wrap(name.getBytes(StandardCharsets.UTF_8)))) {
                return true;
            }
        }

        return false;
    }

    public boolean containsMemberId(ByteArray identifier) {
        for (PapayaMember member : members) {
            if (member.getIdentifier().equals(identifier)) {
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
            function.compile(compilationScope);
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

    public void checkWriteAccess(PapayaMember member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        AccessModifier modifier = member.getAccessModifier();
        if (modifier == AccessModifier.ReadOnly) {
            throw new PapayaIllegalAccessException();
        }

        if (!stackTrace.peek().getIdentifier().equals(getIdentifier())) {
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

    public PapayaHandler checkMemberAccess(PapayaMember classMember, PapayaObject member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        AccessModifier modifier = classMember.getAccessModifier();
        // if it is read only, then we return a wrapper object.
        if (modifier == AccessModifier.PublicAccess) {
            return PapayaHandler.doNothingHandler(member);
        } else if (modifier == AccessModifier.ReadOnly) {
            return PapayaHandler.readOnlyHandler(member);
        }

        if (!stackTrace.peek().getIdentifier().equals(getIdentifier())) {
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

        return PapayaHandler.doNothingHandler(member);
    }

    public void checkReadAccess(PapayaMember member, Stack<PapayaStructure> stackTrace) throws PapayaIllegalAccessException {
        AccessModifier modifier = member.getAccessModifier();
        if (modifier == AccessModifier.ReadOnly || modifier == AccessModifier.PublicAccess) {
            return;
        }

        if (!stackTrace.peek().getIdentifier().equals(getIdentifier())) {
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
            if (parent.getIdentifier().equals(getIdentifier())) {
                return true;
            }

            return parent.isChildOf(parent);
        }

        return false;
    }

    public PapayaMember getMember(ByteArray identifier) {
        for (PapayaMember member : members) {
            if (member.getIdentifier().equals(identifier)) {
                return member;
            }
        }

        return null;
    }

    public final ByteArray getIdentifier() {
        return identifier;
    }

    public StructureType getStructureType() {
        return structureType;
    }

    public String getName() {
        return new String(identifier.getArray());
    }

    public Set<PapayaMember> getMembers() {
        return members;
    }

    public BigInteger getIdentifierInt() {
        return new BigInteger(1, getIdentifier().getArray());
    }

    public static final class Operator {
        public static final int
                None = 0,

                Add = 1,
                Sub = 2,
                Mul = 3,
                Div = 4,
                Mod = 5,
                Pow = 6,

                And = 7,
                Or = 8,
                Xor = 9,

                UnsignedShift = 10,
                RightShift = 11,
                LeftShift = 12,

                Not = 13,
                Negate = 14;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append("\n");
        for (PapayaMember member : members) {
            builder.append(member.toString());
        }

        return builder.toString();
    }
}
