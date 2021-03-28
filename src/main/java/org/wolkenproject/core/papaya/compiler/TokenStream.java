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
        Token token = new Token(builder.toString(), type, line, offset);

        tokenList.add(token);
    }
}
