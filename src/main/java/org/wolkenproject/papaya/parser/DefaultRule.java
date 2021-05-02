package org.wolkenproject.papaya.parser;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.ParseRule;
import org.wolkenproject.papaya.compiler.Token;
import org.wolkenproject.papaya.compiler.TokenStream;

public class DefaultRule extends ParseRule {
    public DefaultRule(String nameType) {
        super(nameType);
    }

    @Override
    public Node parse(TokenStream stream, DynamicParser rules) throws PapayaException {
        int mark = stream.mark();
        if (!stream.hasNext()) {
            return null;
        }

        Token token = stream.next();
        if (token.getTokenType().equals(getName())) {
            return new Node(getName(), "default", token);
        }

        stream.jump(mark);
        return null;
    }

    @Override
    public String toString() {
        return "DefaultRule{" +
                "nameType:'" + getName() + '\'' +
                '}';
    }

    @Override
    public String toSimpleString(DynamicParser parser) {
        return getName();
    }
}
