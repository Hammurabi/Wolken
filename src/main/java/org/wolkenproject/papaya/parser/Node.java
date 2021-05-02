package org.wolkenproject.papaya.parser;

import org.wolkenproject.papaya.compiler.LineInfo;
import org.wolkenproject.papaya.compiler.Token;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String tokenRule;
    private final String ruleVariation;
    private final Token token;
    private final List<Node> children;

    public Node(String tokenRule, String variation) {
        this(tokenRule, variation, new Token("", "", new LineInfo(-2, -2)));
    }

    public Node(String tokenRule, String ruleVariation, Token token) {
        this(tokenRule, ruleVariation, token, new ArrayList<>());
    }

    public Node(String tokenRule, String ruleVariation, Token token, List<Node> children) {
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

    public List<Node> getChildren() {
        return children;
    }

    public void add(Node token) {
        children.add(token);
    }

    public void toString(int indent, StringBuilder builder) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < indent; i ++) {
            indentation.append("\t");
        }

        builder.append(indentation);
        builder.append(tokenRule);
        if (!ruleVariation.equals("default")) {
            builder.append(" ").append(ruleVariation);
        }
        if (!token.isEmpty()) {
            builder.append(" ").append(token.getTokenValue());
        }
        builder.append(":").append("\n");
        for (Node token : children) {
            token.toString(indent + 1, builder);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        toString(0, builder);

        return builder.toString();
    }

    public LineInfo getLineInfo() {
        return token.getLineInfo();
    }

    public void add(List<Node> list) {
        children.addAll(list);
    }
}
