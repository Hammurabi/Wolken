package org.wolkenproject.papaya.parser;

import org.wolkenproject.papaya.compiler.LineInfo;
import org.wolkenproject.papaya.compiler.Token;

import java.util.ArrayList;
import java.util.List;

public class ParseToken {
    private final String tokenRule;
    private final String ruleVariation;
    private final Token token;
    private final List<ParseToken> children;

    public ParseToken(String tokenRule, String variation) {
        this(tokenRule, variation, new Token("", "", new LineInfo(-2, -2)));
    }

    public ParseToken(String tokenRule, String ruleVariation, Token token) {
        this(tokenRule, ruleVariation, token, new ArrayList<>());
    }

    public ParseToken(String tokenRule, String ruleVariation, Token token, List<ParseToken> children) {
        this.tokenRule = tokenRule;
        this.ruleVariation = ruleVariation;
        this.token = token;
        this.children = children;
    }

    public String getTokenRule() {
        return tokenRule;
    }

    public Token getToken() {
        return token;
    }

    public List<ParseToken> getChildren() {
        return children;
    }

    public void add(ParseToken token) {
        children.add(token);
    }

    public void toString(int indent, StringBuilder builder) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < indent; i ++) {
            indentation.append("\t");
        }

        builder.append(indentation);
        builder.append(tokenRule).append(" ").append(ruleVariation).append(":").append("\n");
        if (!token.isEmpty()) {
            builder.append(indentation).append(token.getTokenValue()).append(" ").append(token.getTokenType()).append("\n");
        }
        for (ParseToken token : children) {
            token.toString(indent + 1, builder);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        toString(0, builder);

        return builder.toString();
    }
}
