package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.exceptions.WolkenException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TokenStream {
    private List<Token> tokenList;

    public TokenStream() {
        this.tokenList = new ArrayList<>();
    }

    public void add(StringBuilder builder, int line, int offset, Map<String, TokenType> typeMap) throws WolkenException {
    }
}
