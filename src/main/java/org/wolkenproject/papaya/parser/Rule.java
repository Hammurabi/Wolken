package org.wolkenproject.papaya.parser;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.TokenStream;

public interface Rule {
    Node parse(final TokenStream stream, DynamicParser rules, ParseResult result) throws PapayaException;
    int length(DynamicParser parser) throws PapayaException;

    String toSimpleString(DynamicParser parser);
}
