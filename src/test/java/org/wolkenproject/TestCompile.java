package org.wolkenproject;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.wolkenproject.core.ResourceManager;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.papaya.ParseRule;
import org.wolkenproject.papaya.compiler.AbstractSyntaxTree;
import org.wolkenproject.papaya.compiler.PapayaLexer;
import org.wolkenproject.papaya.compiler.TokenStream;
import org.wolkenproject.papaya.parser.DynamicParser;
import org.wolkenproject.papaya.compiler.Compiler;

import java.io.IOException;

public class TestCompile {
    @Test
    public void testCompile() throws WolkenException, IOException, PapayaException {
        JSONObject papaya = ResourceManager.getJson("/papaya/papaya.json");
        PapayaLexer lexer = new PapayaLexer();
        DynamicParser dynamicParser = new DynamicParser(ResourceManager.getJson("/papaya/grammar.json"), papaya.getJSONArray("tokens"));

        String program = ResourceManager.getString("/papaya/contract.pya");
        TokenStream stream = lexer.ingest(program, papaya.getJSONArray("tokens"));
        AbstractSyntaxTree ast = dynamicParser.parse(stream);
    }
}
