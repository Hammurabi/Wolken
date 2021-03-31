package org.wolkenproject.papaya.compiler;

import java.util.ArrayList;
import java.util.List;

public class Token {
    private final String    tokenValue;
    private final TokenType tokenType;
    private final LineInfo  lineInfo;
    private final List<Token> children;

    public Token(String value, TokenType type, int line, int offset) {
        this(value, type, new LineInfo(line, offset));
    }

    public Token(String value, TokenType type, LineInfo lineInfo) {
        this.tokenValue = value;
        this.tokenType  = type;
        this.lineInfo = lineInfo;
        this.children   = new ArrayList<>();
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public int getLine() {
        return lineInfo.getLine();
    }

    public int getOffset() {
        return lineInfo.getOffset();
    }

    public void add(Token token) {
        children.add(token);
    }

    @Override
    public String toString() {
        return "{" +
                "'" + tokenValue + '\'' +
                ", " + tokenType +
                ", lineInfo=" + lineInfo +
                '}';
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public void addChildren(List<Token> tokens) {
        children.addAll(tokens);
    }
}
