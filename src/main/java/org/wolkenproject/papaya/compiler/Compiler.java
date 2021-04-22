package org.wolkenproject.papaya.compiler;

import org.json.JSONObject;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;

import java.io.IOException;

public abstract class Compiler {
    public static Compiler getFor(String language) throws WolkenException, IOException {
        if (language.toLowerCase().equals("papaya")) {
            language = "papaya+v0.01a";
        }

        if (language.toLowerCase().equals("papaya+v0.01a")) {
            return new PapayaCompiler(new PapayaLexer(), new PapayaParser());
        }

        throw new WolkenException("compiler for language '" + language + "' could not be found.");
    }

    public abstract PapayaApplication compile(String text, JSONObject compilerArguments) throws PapayaException, WolkenException;
}
