package org.wolkenproject.core.papaya.compiler;

import java.util.ArrayList;
import java.util.List;

public class TokenStream {
    private List<Token> tokenList;
    private int index;

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

    public boolean hasNext() {
        return index < tokenList.size();
    }

    public boolean hasPrevious() {
        return index >= 0;
    }

    public Token next() {
        return tokenList.get(index ++);
    }

    public Token previous() {
        return tokenList.get(index - 1);
    }

    public boolean matches(TokenType ...pattern) {
        if (index + pattern.length < tokenList.size()) {
            for (int i = 0; i < pattern.length; i ++) {
                if (tokenList.get(i + index).getTokenType() != pattern[i]) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
