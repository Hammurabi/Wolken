package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.exceptions.WolkenException;

public class Compiler {
    public static Compiler getFor(String language) throws WolkenException {
        throw new WolkenException("compiler for language '" + language + "' could not be found.");
    }
}
