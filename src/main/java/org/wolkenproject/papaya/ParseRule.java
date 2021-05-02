package org.wolkenproject.papaya;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.TokenStream;
import org.wolkenproject.papaya.parser.*;

import java.util.*;

public class ParseRule {
    private final String        ruleName;
    private final List<Subrule> options;

    protected ParseRule(String name) {
        this.ruleName = name;
        this.options = null;
    }

    public ParseRule(String name, List<List<Rule>> options) {
        this.ruleName = name;
        this.options = new ArrayList<>();
        for (List<Rule> option : options) {
            this.options.add(new Subrule(name, "", option));
        }
    }

    public ParseRule(JSONObject rule) {
        this.ruleName = rule.keys().next();
        this.options  = new ArrayList<>();

        for (int i = 0; i < rule.getJSONArray(ruleName).length(); i ++) {
            JSONArray array = rule.getJSONArray(ruleName).getJSONArray(i);
            options.add(new Subrule(ruleName, "[" + i + "]", array));
        }
    }

    public ParseResult parse(TokenStream stream, DynamicParser rules, Queue<ParseResult> results) throws PapayaException {
        for (Subrule option : options) {
            int mark = stream.mark();
            ParseResult res = new ParseResult(this);
            Node token = option.parse(stream, rules, res);
            res.setResult(token);
            res.jump(stream, mark);
            results.add(res);

            if (token != null) {
                return res;
            }

            stream.jump(mark);
        }

        return null;
    }

    public Node parse(TokenStream stream, DynamicParser rules, ParseResult result) throws PapayaException {
        Queue<ParseResult> results = new LinkedList<>();
        ParseResult pRes = parse(stream, rules, results);
        result.add(results.poll());

        if (pRes != null) {
            return pRes.getParseResult();
        }

        return null;
    }

    public String getName() {
        return ruleName;
    }

    @Override
    public String toString() {
        return "ParseRule{" +
                "ruleName:'" + ruleName + '\'' +
                ", options:" + options +
                '}';
    }

    public int length(DynamicParser parser) throws PapayaException {
        int length = 0;
        if (options == null) return 0;

        for (Subrule rule : options) {
            length += rule.length(parser);
        }

        return length;
    }

    public void sort() {
        Collections.sort(options);
    }

    public String toSimpleString(DynamicParser parser) {
        return toString();
    }
}
