package org.wolkenproject.core.papaya.compiler;

public class Token {
    private final String    tokenValue;
    private final TokenType tokenType;
    private final int       line;
    private final int       offset;

    public Token(String value, TokenType type, int line, int offset) {
        this.tokenValue = value;
        this.tokenType  = type;
        this.line       = line;
        this.offset     = offset;
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
