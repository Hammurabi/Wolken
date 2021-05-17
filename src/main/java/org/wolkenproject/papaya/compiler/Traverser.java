package org.wolkenproject.papaya.compiler;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.parser.Node;

public interface Traverser {
    void onEnter(Node node, FunctionScope functionScope, CompilationScope compilationScope) throws PapayaException;
}