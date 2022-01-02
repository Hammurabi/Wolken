package org.wolkenproject.papaya.runtime;

import org.wolkenproject.papaya.compiler.AccessModifier;
import org.wolkenproject.utils.ByteArray;

public class Member {
    private final ByteArray         identifier;
    private final AccessModifier    accessModifier;
    private final boolean           isStatic;

    public Member(ByteArray identifier, AccessModifier accessModifier, boolean isStatic) {
        this.identifier = identifier;
        this.accessModifier = accessModifier;
        this.isStatic = isStatic;
    }
}
