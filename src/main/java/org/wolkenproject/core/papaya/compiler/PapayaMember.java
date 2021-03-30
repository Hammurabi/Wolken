package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.core.papaya.AccessModifier;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;
import java.util.Arrays;

public class PapayaMember {
    private final AccessModifier    accessModifier;
    private final String            name;
    private byte[]                  identifier;

    public PapayaMember(AccessModifier accessModifier, String name) {
        this.accessModifier = accessModifier;
        this.name = name;
        this.identifier = new byte[16];
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    public String getName() {
        return name;
    }

    public void setIdentifier(int identifier) {
        this.identifier = Utils.takeApart(identifier);
    }

    public void setIdentifier(long identifier) {
        this.identifier = Utils.takeApartLong(identifier);
    }

    public void setIdentifier(byte identifier[]) {
        this.identifier = Arrays.copyOf(identifier, 16);
    }

    public byte[] getIdentifier() {
        return identifier;
    }

    public BigInteger getIdentifierInt() {
        return new BigInteger(1, getIdentifier());
    }
}
