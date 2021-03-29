package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.exceptions.WolkenException;

import static org.wolkenproject.core.papaya.compiler.TokenType.*;

public class PapayaParser {
    public Token ingest(TokenStream stream) throws WolkenException {
        return recursive(stream);
    }

    private Token recursive(TokenStream stream) throws WolkenException {
        while (stream.hasNext()) {
            if (stream.matches(ContractKeyword, Identifier)) { // contact declaration
            } else if (stream.matches(ClassKeyword, Identifier)) { // class declaration
            } else if (stream.matches(StructKeyword, Identifier)) { // struct declaration
            } else if (stream.matches(FunctionKeyword, Identifier)) { // function declaration
            } else if (stream.matches(Identifier, Identifier)) { // field declaration
            } else if (stream.matches(Identifier, SemiColonEqualsSymbol)) { // local field declaration a:=b
            }
        }

        return null;
    }
}
