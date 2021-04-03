package org.wolkenproject.papaya.compiler;

import org.json.JSONObject;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;

public class PapayaCompiler extends Compiler {
    private PapayaLexer lexer;
    private PapayaParser parser;

    public PapayaCompiler(PapayaLexer papayaLexer, PapayaParser papayaParser) {
        super();
        this.lexer = papayaLexer;
        this.parser= papayaParser;
    }

    @Override
    public PapayaApplication compile(String text, JSONObject compilerArguments) throws PapayaException, WolkenException {
        PapayaApplication application = parser.ingest(lexer.ingest(text));
        application.compile();

        return application;
    }
}
