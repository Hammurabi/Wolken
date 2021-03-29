package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.exceptions.WolkenException;

import static org.wolkenproject.core.papaya.compiler.TokenType.*;

public class PapayaParser {
    public ApplicationBinaryInterface ingest(TokenStream stream) throws WolkenException {
        return parseABI(stream);
    }

    private ApplicationBinaryInterface parseABI(TokenStream stream) throws WolkenException {
        while (stream.hasNext()) {
            if (stream.matches(ContractKeyword, Identifier)) { // contact declaration
                Token keyword   = stream.next();
                Token name      = stream.next();

                // check for inheritance.
                if (stream.matches(ExtendsKeyword) || stream.matches(ImplementsKeyword)) {
                }

                if (!stream.matches(LeftBraceSymbol)) {
                    throw new WolkenException("expected an '{' at line: " + keyword.getLine() + ".");
                }

                // get a new token stream containing all the body tokens.
                TokenStream body= getTokensFollowing(LeftBraceSymbol, stream);

                // parse the body
            } else if (stream.matches(ClassKeyword, Identifier)) { // class declaration
            } else if (stream.matches(StructKeyword, Identifier)) { // struct declaration
            } else if (stream.matches(FunctionKeyword, Identifier)) { // function declaration
            } else if (stream.matches(Identifier, Identifier)) { // field declaration
            } else if (stream.matches(Identifier, SemiColonEqualsSymbol)) { // local field declaration a:=b
            } else {
                throw new WolkenException("cannot parse unknown pattern '" + stream + "'.");
            }
        }

        return null;
    }

    private TokenStream getTokensFollowing(TokenType opener, TokenStream stream) throws WolkenException {
        TokenStream result  = new TokenStream();
        TokenType closer    = None;
        switch (opener) {
            case LeftParenthesisSymbol:
                closer = RightParenthesisSymbol;
                break;
            case LeftBraceSymbol:
                closer = RightBraceSymbol;
                break;
            case LeftBracketSymbol:
                closer = RightBracketSymbol;
                break;
            default:
                throw new WolkenException("invalid usage of token '" + opener + "' is never closed.");
        }

        int numOpened = 1;
        int numClosed = 0;

        while (stream.hasNext()) {
            Token next = stream.next();
            if (next.getTokenType() == opener) {
                numOpened ++;
            } else if (next.getTokenType() == closer) {
                numClosed ++;
            }

            if (numOpened == numClosed) {
                return result;
            } else {
                result.add(next);
            }
        }

        throw new WolkenException("token '" + opener + "' is never closed.");
    }
}
