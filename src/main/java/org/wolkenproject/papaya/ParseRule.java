package org.wolkenproject.papaya;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.TokenStream;
import org.wolkenproject.papaya.parser.DynamicParser;
import org.wolkenproject.papaya.parser.Rule;
import org.wolkenproject.papaya.parser.Subrule;
import org.wolkenproject.papaya.parser.ParseToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ParseRule {
    private final String        ruleName;
    private final List<Subrule> options;

    protected ParseRule(String name) {
        this.ruleName = name;
        this.options = null;
    }

    public ParseRule(JSONObject rule) {
        this.ruleName = rule.keys().next();
        this.options  = new ArrayList<>();

        for (int i = 0; i < rule.getJSONArray(ruleName).length(); i ++) {
            JSONArray array = rule.getJSONArray(ruleName).getJSONArray(i);
            options.add(new Subrule(ruleName, "[" + i + "]", array));
        }
    }

    public ParseToken parse(TokenStream stream, DynamicParser rules) throws PapayaException {
        for (Subrule option : options) {
            int mark = stream.mark();
            ParseToken token = option.parse(stream, rules);

            if (token != null) {
                return token;
            }

            stream.jump(mark);
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
}
