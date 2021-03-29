package org.wolkenproject.core.papaya.compiler;

import org.json.JSONObject;
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
    public CompiledScript compile(String text, JSONObject compilerArguments) throws WolkenException {
        Token root = parser.ingest(lexer.ingest(text));

        return null;
    }
}
