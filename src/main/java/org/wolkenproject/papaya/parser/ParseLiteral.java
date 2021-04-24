package org.wolkenproject.papaya.parser;

import org.wolkenproject.papaya.compiler.Token;
import org.wolkenproject.papaya.compiler.TokenStream;

public class ParseLiteral implements Rule {
    private final String string;

    public ParseLiteral(String string) {
        this.string = string;
    }

    @Override
    public ParseToken parse(TokenStream stream, DynamicParser rules) {
        Token check = stream.next();
        if (check.getTokenValue().equals(string)) {
            return new ParseToken(string, "default", check);
        }

        return null;
    }

    @Override
    public int length(DynamicParser parser) {
        return 1;
    }

    @Override
    public String toString() {
        return "ParseLiteral{" +
                "string:'" + string + '\'' +
                '}';
    }
}
