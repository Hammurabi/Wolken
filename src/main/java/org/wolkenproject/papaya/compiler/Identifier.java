package org.wolkenproject.papaya.compiler;

import org.wolkenproject.utils.ByteArray;

public class Identifier {
    private final String          identifierString;
    private final ByteArray       value;
    private final IdentifierType  type;

    public Identifier(String identifierString, ByteArray value, IdentifierType type) {
        this.identifierString = identifierString;
        this.value = value;
        this.type = type;
    }

    public String getIdentifierString() {
        return identifierString;
    }

    public ByteArray getValue() {
        return value;
    }

    public IdentifierType getType() {
        return type;
    }
}
