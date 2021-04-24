package org.wolkenproject.papaya.parser;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.TokenStream;

public class ParseRuleReference implements Rule {
    private final String ruleName;

    public ParseRuleReference(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public ParseToken parse(TokenStream stream, DynamicParser rules) throws PapayaException {
        return rules.getRule(ruleName).parse(stream, rules);
    }

    @Override
    public int length(DynamicParser parser) throws PapayaException {
        return parser.getRule(ruleName).length(parser);
    }

    @Override
    public String toString() {
        return "ParseRuleReference{" +
                "rulename:'" + ruleName + '\'' +
                '}';
    }
}
