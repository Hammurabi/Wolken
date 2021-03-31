package org.wolkenproject.papaya.compiler;

import java.util.ArrayList;
import java.util.List;

public class Token {
    private final String    tokenValue;
    private final TokenType tokenType;
    private final int       line;
    private final int       offset;
    private final List<Token> children;

    public Token(String value, TokenType type, int line, int offset) {
        this.tokenValue = value;
        this.tokenType  = type;
        this.line       = line;
        this.offset     = offset;
        this.children   = new ArrayList<>();
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public int getLine() {
        return line;
    }

    public int getOffset() {
        return offset;
    }

    public void add(Token token) {
        children.add(token);
    }

    @Override
    public String toString() {
        return "{" +
                "'" + tokenValue + '\'' +
                ", " + tokenType +
                ", line=" + line +
                ", offset=" + offset +
                '}';
    }

    public LineInfo getLineInfo() {
        return new LineInfo(line, offset);
    }
}
