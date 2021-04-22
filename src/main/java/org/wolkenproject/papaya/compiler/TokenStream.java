package org.wolkenproject.papaya.compiler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wolkenproject.exceptions.PapayaException;

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

    public boolean matches(String ...pattern) {
        if (index + pattern.length <= tokenList.size()) {
            for (int i = 0; i < pattern.length; i ++) {
                if (!tokenList.get(i + index).getTokenType().equals(pattern[i])) {
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

    public int matchesRule(JSONArray rule, JSONObject rules, int index) throws PapayaException {
        for (int i = 0; i < rule.length(); i ++) {
            int isMatching = -1;
            JSONArray option = rule.getJSONArray(i);
            for (int x = 0; x < option.length(); x ++) {
                if (index + x >= tokenList.size()) {
                    isMatching = -1;
                    break;
                }

                int tokenIndex = index + x;

                String value = option.getString(x);
                if (isLiteral(value)) {
                    if (!tokenList.get(tokenIndex).getTokenValue().equals(value.substring(1, value.length() - 1))) {
                        isMatching = -1;
                        break;
                    }

                    isMatching ++;
                } else {
                    if (rules.has(value)) {
                        int match = matchesRule(rules.getJSONArray(value), rules, tokenIndex);
                        if (match < 0) {
                            isMatching = -1;
                            break;
                        }

                        isMatching = match;
                    } else {
                        if (!checkBasicRule(value, tokenList.get(tokenIndex))) {
                            isMatching = -1;
                            break;
                        }

                        isMatching ++;
                    }
                }
            }

            if (isMatching >= 0) {
                return isMatching;
            }
        }

        return -1;
    }

    private boolean checkBasicRule(String ruleName, Token token) throws PapayaException {
        return ruleName.toLowerCase().equals(token.getTokenType());
    }

    public ParseToken match(JSONObject grammar) throws PapayaException {
        for (String ruleName : grammar.keySet()) {
            JSONArray rule = grammar.getJSONArray(ruleName);

            int match = matchesRule(rule, grammar, index);

            if (match >= 0) {
            }
        }

        return null;
    }
}
