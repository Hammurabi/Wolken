package org.wolkenproject.papaya.compiler.grammar;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.TokenStream;
import org.wolkenproject.papaya.parser.DynamicParser;
import org.wolkenproject.papaya.parser.Node;
import org.wolkenproject.papaya.parser.ParseResult;
import org.wolkenproject.papaya.parser.Rule;

import java.util.ArrayList;
import java.util.List;

public class ZeroOrMore implements Rule {
    private final Rule rule;

    public ZeroOrMore(Rule rule) {
        this.rule = rule;
    }

    @Override
    public Node parse(TokenStream stream, DynamicParser rules) throws PapayaException {
        List<Node> list = new ArrayList<>();
        Node node = null;

        do {
            node = rule.parse(stream, rules);
            if (node != null) {
                list.add(node);
            }
        } while (node != null);

        String name = "empty";
        if (!list.isEmpty()) {
            name = list.get(0).getTokenRule();
        }

        Node zom = new Node(name + "*", "");
        zom.add(list);
        return zom;
    }

    @Override
    public int length(DynamicParser parser) throws PapayaException {
        return 0;
    }

    @Override
    public String toSimpleString(DynamicParser parser) {
        return rule.toSimpleString(parser) + "*";
    }

    @Override
    public String toString() {
        return "ZeroOrMore{" +
                "rule=" + rule +
                '}';
    }
}
