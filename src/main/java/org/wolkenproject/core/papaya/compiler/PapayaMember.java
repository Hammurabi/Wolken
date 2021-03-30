package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.core.papaya.AccessModifier;

public class PapayaMember {
    private final AccessModifier accessModifier;

    public PapayaMember(AccessModifier accessModifier) {
        this.accessModifier = accessModifier;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }
}
