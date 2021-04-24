package org.wolkenproject.papaya.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.ParseRule;
import org.wolkenproject.papaya.compiler.AbstractSyntaxTree;
import org.wolkenproject.papaya.compiler.TokenStream;

import java.util.*;

public class DynamicParser implements Parser {
    private final List<ParseRule>           parseRules;
    private final Map<String, ParseRule>    references;

    public DynamicParser(JSONObject rules, JSONArray tokens) throws PapayaException {
        this.parseRules = new ArrayList<>();
        this.references = new HashMap<>();

        JSONArray parseRuleArray    = rules.getJSONArray("parse");
        JSONArray subs              = rules.getJSONArray("rule");

        for (int i = 0; i < parseRuleArray.length(); i ++) {
            ParseRule rule = new ParseRule(parseRuleArray.getJSONObject(i));
            parseRules.add(rule);
            references.put(rule.getName(), rule);
        }

        for (int i = 0; i < subs.length(); i ++) {
            ParseRule rule = new ParseRule(subs.getJSONObject(i));
            references.put(rule.getName(), rule);
        }

        for (int i = 0; i < tokens.length(); i ++) {
            String nameType = tokens.getJSONObject(i).keys().next();
            references.put(nameType, new DefaultRule(nameType));
        }
    }

    public ParseRule getRule(String name) throws PapayaException {
        if (references.containsKey(name)) {
            return references.get(name);
        }

        throw new PapayaException("rule '"+name+"' not found.");
    }

    @Override
    public AbstractSyntaxTree parse(TokenStream stream) throws PapayaException {
        AbstractSyntaxTree abstractSyntaxTree = new AbstractSyntaxTree();
        while (stream.hasNext()) {
            ParseToken token = null;
            for (ParseRule rule : parseRules) {
                token = rule.parse(stream, this);
                if (token != null) {
                    break;
                }
            }

            if (token == null) {
                throw new PapayaException("could not parse unidentified string: " + stream);
            }

            abstractSyntaxTree.add(token);
        }

        return abstractSyntaxTree;
    }

    public Collection<ParseRule> getRules() {
        return references.values();
    }
}
