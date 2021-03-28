package org.wolkenproject.core.papaya.compiler;

import static org.wolkenproject.core.papaya.compiler.TokenType.None;

public class PapayaLexer {
    public TokenStream ingest(String data) {
        TokenStream tokenStream = new TokenStream();

        StringBuilder   builder = new StringBuilder();
        TokenType       type    = None;
        char            lastChar= '\0';

        for (char character : data.toCharArray()) {
            if (lastChar == ' ') {
            } else {
            }

            lastChar = character;
        }
    }
}
