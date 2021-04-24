package org.wolkenproject.papaya.parser;

import org.json.JSONArray;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.ParseRule;
import org.wolkenproject.papaya.compiler.TokenStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Subrule implements Rule, Comparable<Subrule> {
    private final String ruleName;
    private final String ruleExt;
    private final List<Rule> rules;
    private int length;

    public Subrule(String name, String rext, JSONArray array) {
        ruleName = name;
        ruleExt  = rext;
        rules = new ArrayList<>();
        for (int i = 0; i < array.length(); i ++) {
            Object o = array.get(i);
            if (o instanceof String) {
                if (((String) o).startsWith("'") && ((String) o).endsWith("'")) {
                    rules.add(new ParseLiteral(((String) o).substring(1, ((String) o).length() - 1)));
                } else {
                    rules.add(new ParseRuleReference((String) o));
                }
            } else if (o instanceof JSONArray) {
                rules.add(new Subrule(name, ruleExt + "_", (JSONArray) o));
            }
        }
    }

    @Override
    public ParseToken parse(TokenStream stream, DynamicParser rules) throws PapayaException {
        ParseToken option = new ParseToken(ruleName, ruleExt);
        if (this.rules.isEmpty()) {
            return option;
        }

        for (Rule rule : this.rules) {
            ParseToken token = rule.parse(stream, rules);
            if (token == null) {
                return null;
            }

            option.add(token);
        }

        return option;
    }

    @Override
    public String toString() {
        return "sub:" + rules.toString();
    }

    @Override
    public int length(DynamicParser parser) throws PapayaException {
        int length = 0;
        for (Rule rule : rules) {
            length += rule.length(parser);
        }

        this.length = length;
        return length;
    }

    @Override
    public int compareTo(Subrule o) {
        return length > o.length ? -1 : 1;
    }
}
