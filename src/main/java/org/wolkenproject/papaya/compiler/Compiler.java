package org.wolkenproject.papaya.compiler;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.papaya.archive.ArchivedMember;
import org.wolkenproject.papaya.archive.ArchivedStruct;
import org.wolkenproject.papaya.archive.PapayaArchive;
import org.wolkenproject.papaya.parser.Node;
import org.wolkenproject.utils.ByteArray;

import java.io.IOException;
import java.util.Map;

public abstract class Compiler {
    private Map<String, ByteArray> typeNames;

    public static Compiler getFor(String language) throws WolkenException, IOException, PapayaException {
        if (language.toLowerCase().equals("papaya")) {
            language = "papaya+v0.01a";
        }

        if (language.toLowerCase().equals("papaya+v0.01a")) {
            return new PapayaCompiler(Context.getInstance().getOpcodeRegister());
        }

        throw new WolkenException("compiler for language '" + language + "' could not be found.");
    }

    public abstract PapayaArchive createArchive(String text, String compilerArguments) throws PapayaException, WolkenException, IOException;
    public abstract PapayaApplication compile(PapayaArchive archive, PapayaArchive libraries, String compilerArguments) throws PapayaException, WolkenException, IOException;

    public ByteArray uniqueTypename(String type) {
        return typeNames.get(type);
    }

    public abstract Expression compile(ArchivedStruct parent, ArchivedMember archivedMember, Node expression) throws PapayaException;
}
