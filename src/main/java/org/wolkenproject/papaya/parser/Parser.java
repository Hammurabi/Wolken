package org.wolkenproject.papaya.parser;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.AbstractSyntaxTree;
import org.wolkenproject.papaya.compiler.TokenStream;

public interface Parser {
    AbstractSyntaxTree parse(TokenStream stream) throws PapayaException;
}
