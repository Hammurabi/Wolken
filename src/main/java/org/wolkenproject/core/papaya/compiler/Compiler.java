package org.wolkenproject.core.papaya.compiler;

import org.json.JSONObject;
import org.wolkenproject.exceptions.WolkenException;

public abstract class Compiler {
    public static Compiler getFor(String language) throws WolkenException {
        throw new WolkenException("compiler for language '" + language + "' could not be found.");
    }

    public abstract CompiledScript compile(String text, JSONObject compilerArguments);
}
