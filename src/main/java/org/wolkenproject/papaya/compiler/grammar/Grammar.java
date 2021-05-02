package org.wolkenproject.papaya.compiler.grammar;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.ParseRule;
import org.wolkenproject.papaya.compiler.TokenStream;
import org.wolkenproject.papaya.parser.*;

import java.util.*;

public class Grammar {
    private Map<String, Object> map;

    public Grammar(String cfg) throws PapayaException {
        map = new HashMap<>();
        Stack<Map<String, Object>> stack = new Stack<>();
        stack.push(map);
        int ws = 0;
        String split[] = cfg.split("\n");
        GrammarRule rule = null;

        for (String line : split) {
            int whiteSpace = line.indexOf(line.trim()) / 2;

            if (whiteSpace < ws && stack.size() > 1) {
                int dif = ws - whiteSpace;
                for (int i = 0; i < dif; i ++) {
                    stack.pop();
                }
                ws = whiteSpace;
            }

            String no_whitespace = line.trim();

            if (no_whitespace.endsWith(":")) {
                GrammarRule object = new GrammarRule(no_whitespace.substring(0, no_whitespace.length() - 1));
                stack.peek()
                        .put(no_whitespace.substring(0, no_whitespace.length() - 1), object);
                stack.push(object.asMap());
                ws ++;
                rule = object;
                continue;
            }

            String words[] = line.trim().split(" ");
            List<Rule> list = new ArrayList<>();

            for (int i = 0; i < words.length; i ++) {
                String string = words[i];

                if (whiteSpace == 0) {
                    throw new PapayaException("invalid grammar file indentation: " + line);
                }

                boolean one_or_more = string.endsWith("+");
                boolean zero_or_more = string.endsWith("*");
                if (one_or_more || zero_or_more) {
                    string = string.substring(0, string.length() - 1);
                }

                if (string.startsWith(":0")) {
                    break;
                } else if (string.startsWith("'") && string.endsWith("'")) {
                    String actual_string = string.substring(1, string.length() - 1);
                    list.add(string(actual_string, one_or_more, zero_or_more));
                } else {
                    list.add(rule(string, one_or_more, zero_or_more));
                }
            }

            rule.add(list);
        }
    }

    private Rule rule(String string, boolean one_or_more, boolean zero_or_more) {
        Rule rule = new ParseRuleReference(string);

        if (one_or_more) {
            return new OneOrMore(rule);
        }

        if (zero_or_more) {
            return new ZeroOrMore(rule);
        }

        return rule;
    }

    private Rule string(String actual_string, boolean one_or_more, boolean zero_or_more) {
        Rule rule = new ParseLiteral(actual_string);

        if (one_or_more) {
            return new OneOrMore(rule);
        }

        if (zero_or_more) {
            return new ZeroOrMore(rule);
        }

        return rule;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            builder.append(key).append(" ").append(map.get(key)).append("\n");
        }

        return builder.toString();
    }

    public void getRules(Map<String, ParseRule> references) {
        for (String key : map.keySet()) {
            references.put(key, ((GrammarRule) map.get(key)).asParseRule());
        }
    }
}
