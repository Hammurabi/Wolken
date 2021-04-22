package org.wolkenproject.papaya.compiler;

import org.json.JSONObject;
import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;

import java.io.IOException;

public class PapayaCompiler extends Compiler {
    private PapayaLexer lexer;
    private PapayaParser parser;
    private JSONObject grammar;
    private JSONObject tokens;

    public PapayaCompiler(PapayaLexer papayaLexer, PapayaParser papayaParser) throws IOException {
        super();
        this.lexer = papayaLexer;
        this.parser= papayaParser;
        this.tokens = Context.getInstance().getResourceManager().getJson("/papaya/tokens.json");
        this.grammar = Context.getInstance().getResourceManager().getJson("/papaya/grammar.json");
    }

    @Override
    public PapayaApplication compile(String text, JSONObject compilerArguments) throws PapayaException, WolkenException {
        AbstractSyntaxTree ast = parser.ingest(lexer.ingest(text, tokens), grammar);

        return null;
    }
}
