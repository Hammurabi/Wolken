package org.wolkenproject.papaya.compiler;

import org.wolkenproject.papaya.parser.Node;

public class AbstractSyntaxTree {
    public Node root;

    public AbstractSyntaxTree() {
        root = new Node("root", "");
    }

    public void add(Node token) {
        root.add(token);
    }

    public Node getRoot() {
        return root;
    }
}
