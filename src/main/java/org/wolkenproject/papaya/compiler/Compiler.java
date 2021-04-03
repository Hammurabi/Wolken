package org.wolkenproject.papaya.compiler;

import org.json.JSONObject;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;

public abstract class Compiler {
    public static Compiler getFor(String language) throws WolkenException {
        if (language.toLowerCase().equals("papaya+v0.01a")) {
            return new PapayaCompiler(new PapayaLexer(PapayaLexer.getTokenTypes()), new PapayaParser());
        }

        throw new WolkenException("compiler for language '" + language + "' could not be found.");
    }

    public abstract PapayaApplication compile(String text, JSONObject compilerArguments) throws PapayaException, WolkenException;
}
