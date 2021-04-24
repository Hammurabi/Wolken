package org.wolkenproject.papaya.compiler;

import org.wolkenproject.exceptions.PapayaException;

import java.util.*;

public class PapayaParser {
    public AbstractSyntaxTree ingest(TokenStream stream, Map<String, List<List<String>>> grammar) throws PapayaException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree();
        return ast;
    }

    private TokenStream getTokensTilEOL(int currentLine, TokenStream stream) {
        TokenStream result = new TokenStream();

        while (stream.hasNext()) {
            if (stream.peek().getLine() != currentLine) {
                break;
            }

            result.add(stream.next());
        }

        return result;
    }

    private TokenStream getTokensFollowing(String opener, TokenStream stream, String error) throws PapayaException {
        TokenStream result  = new TokenStream();
        String closer    = "";

        if (!stream.matches(opener)) {
            throw new PapayaException(error);
        }

        // skip the opener symbol.
        stream.next();

        switch (opener) {
            case "(":
                closer = ")";
                break;
            case "{":
                closer = "}";
                break;
            case "[":
                closer = "]";
                break;
            default:
                throw new PapayaException("invalid usage of token '" + opener + "' is never closed.");
        }

        int numOpened = 1;
        int numClosed = 0;

        while (stream.hasNext()) {
            Token next = stream.next();
            if (next.getTokenType().equals(opener)) {
                numOpened ++;
            } else if (next.getTokenType().equals(closer)) {
                numClosed ++;
            }

            if (numOpened == numClosed) {
                return result;
            } else {
                result.add(next);
            }
        }

        throw new PapayaException("token '" + opener + "' is never closed.");
    }
}
