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

    public Token getChild(int index) {
        return children.get(index);
    }

    public Token getFirstChildOfType(TokenType type) {
        for (Token token : children) {
            if (token.getTokenType() == type) {
                return token;
            }
        }

        return null;
    }

    public Token setType(TokenType tokenType) {
        return new Token(tokenValue, tokenType, lineInfo);
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
