package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.exceptions.WolkenException;

import java.util.ArrayList;
import java.util.List;

public class TokenStream {
    private List<Token> tokenList;

    public TokenStream() {
        this.tokenList = new ArrayList<>();
    }

    public void add(StringBuilder builder, int line, int offset) throws WolkenException {
        TokenType type = TokenType.None;
        String string = builder.toString();

        if (string.matches("\\d+")) {
        } else if (string.matches("([A-z]|\\_)+\\d*")) {
        }
        
        Token token = new Token(builder.toString(), type, line, offset);
        tokenList.add(token);
    }
}
