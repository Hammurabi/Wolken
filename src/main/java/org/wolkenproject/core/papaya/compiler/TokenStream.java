package org.wolkenproject.core.papaya.compiler;

import java.util.ArrayList;
import java.util.List;

public class TokenStream {
    private List<Token> tokenList;

    public TokenStream() {
        this.tokenList = new ArrayList<>();
    }

    public void add(Token token) {
        tokenList.add(token);
    }

    @Override
    public String toString() {
        return tokenList.toString();
    }
}
