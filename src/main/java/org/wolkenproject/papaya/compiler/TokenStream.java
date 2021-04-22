package org.wolkenproject.papaya.compiler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TokenStream {
    private List<Token> tokenList;
    private int index;

    public TokenStream() {
        this.tokenList = new ArrayList<>();
    }

    public TokenStream(List<Token> tokens, int index) {
        this.tokenList = tokens;
        this.index = index;
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

    public Token peek() {
        return tokenList.get(index);
    }

    public Token previous() {
        return tokenList.get(index - 1);
    }

    public boolean matches(TokenType ...pattern) {
        if (index + pattern.length <= tokenList.size()) {
            for (int i = 0; i < pattern.length; i ++) {
                if (tokenList.get(i + index).getTokenType() != pattern[i]) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public boolean isEmpty() {
        return tokenList.isEmpty();
    }

    private static boolean isLiteral(String rule) {
        return rule.startsWith("'") && rule.endsWith("'");
    }

    public ParseToken match(JSONObject grammar) {
        for (String ruleName : grammar.keySet()) {
            JSONArray rule = grammar.getJSONArray(ruleName);
            int match = matches(grammar, rule);

            if (match >= 0) {
                ParseToken token = new ParseToken(ruleName, peek().getLineInfo());

                return token;
            }
        }

        return null;
    }
}
