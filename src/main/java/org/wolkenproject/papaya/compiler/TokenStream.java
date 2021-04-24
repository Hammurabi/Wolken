package org.wolkenproject.papaya.compiler;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void jump(int mark) {
        this.index = mark;
    }

    public int mark() {
        return index;
    }
}
