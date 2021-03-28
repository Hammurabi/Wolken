package org.wolkenproject.core.papaya.compiler;

public class Token {
    private final String    tokenValue;
    private final TokenType tokenType;

    public Token(String value, TokenType type) {
        this.tokenValue = value;
        this.tokenType  = type;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }
}
