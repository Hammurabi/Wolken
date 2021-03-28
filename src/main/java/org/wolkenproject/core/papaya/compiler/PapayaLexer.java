package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.exceptions.WolkenException;

import java.util.HashMap;
import java.util.Map;

public class PapayaLexer {
    private final Map<String, TokenType> typeMap;

    public PapayaLexer(Map<String, TokenType> typeMap) {
        this.typeMap = typeMap;
    }

    public TokenStream ingest(String data) throws WolkenException {
        TokenStream tokenStream = new TokenStream();

        StringBuilder   builder = new StringBuilder();
        char            lastChar= '\0';
        int             line    = 1;
        int             offset  = 0;

        for (char character : data.toCharArray()) {
            offset ++;

            if (character == '\n') {
                if (builder.length() != 0) {
                    tokenStream.add(getToken(builder, line, offset, typeMap));
                    builder = new StringBuilder();
                }

                offset = 1;
            }

            if (character == ' ') {
                if (builder.length() != 0) {
                    tokenStream.add(getToken(builder, line, offset, typeMap));
                    builder = new StringBuilder();
                }
            }
        }
    }

    private static Token getToken(StringBuilder builder, int line, int offset, Map<String, TokenType> typeMap) throws WolkenException {
        TokenType type = TokenType.None;
        String string = builder.toString();

        for (String regex : typeMap.keySet()) {
            if (string.matches(regex)) {
                return new Token(string, typeMap.get(regex), line, offset);
            }
        }

        throw new WolkenException("could not create token for string '" + string + "'.");
    }

    public static Map<String, TokenType> getTokenTypes() {
        Map<String, TokenType> tokenType = new HashMap<>();
        tokenType.put("\\d+", TokenType.IntegerNumber);
        tokenType.put("[0][b][0-1]+", TokenType.IntegerNumber);
        tokenType.put("0x[\\d|(a|b|c|d|e|f)]+", TokenType.Base16String);
        tokenType.put("\\d+\\.\\d+", TokenType.DecimalNumber);
        tokenType.put("\\d+\\.", TokenType.DecimalNumber);
        tokenType.put("\\.\\d+", TokenType.DecimalNumber);
        tokenType.put("([A-z]|\\_)+\\d*", TokenType.Identifier);
//        tokenType.put("N([A-z]|[1|2|3|4|5|6|7|8|9])+", TokenType.Base58String);

        return tokenType;
    }
}
