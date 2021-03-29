package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.core.papaya.compiler.statements.FieldDeclarationStatement;
import org.wolkenproject.core.papaya.compiler.statements.FunctionCallStatement;
import org.wolkenproject.exceptions.WolkenException;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.wolkenproject.core.papaya.compiler.TokenType.*;

public class PapayaParser {
    public PapayaApplication ingest(TokenStream stream) throws WolkenException {
        return parseABI(stream);
    }

    private PapayaApplication parseABI(TokenStream stream) throws WolkenException {
        PapayaApplication app = new PapayaApplication();

        while (stream.hasNext()) {
            // contract, class, or struct declaration.
            if (stream.matches(ContractKeyword, Identifier) || stream.matches(ClassKeyword, Identifier) || stream.matches(StructKeyword, Identifier)) {
                Token keyword   = stream.next();
                Token name      = stream.next();

                StructureType type = StructureType.ContractType;

                switch (name.getTokenType()) {
                    case ContractKeyword:
                        type = StructureType.ContractType;
                        break;
                    case ClassKeyword:
                        type = StructureType.ClassType;
                        break;
                    case StructKeyword:
                        type = StructureType.StructType;
                        break;
                }

                // check for inheritance.
                if (stream.matches(ExtendsKeyword) || stream.matches(ImplementsKeyword)) {
                }

                // get a new token stream containing all the body tokens.
                TokenStream body= getTokensFollowing(LeftBraceSymbol, stream, "expected an '{' at line: " + keyword.getLine() + ".");

                // create a structure.
                PapayaStructure structure = new PapayaStructure(name.getTokenValue(), StructureType.ContractType, keyword.getLineInfo());

                // parse the body into the structure.
                parseStructure(structure, body);

                // add the structure to the ABI
                app.addStructure(name.getTokenValue(), structure);
            } else {
                throw new WolkenException("cannot parse unknown pattern '" + stream + "' in global scope.");
            }
        }

        return null;
    }

    private void parseStructure(PapayaStructure structure, TokenStream stream) throws WolkenException {
        while (stream.hasNext()) {
            if (stream.matches(FunctionKeyword, Identifier, LeftParenthesisSymbol)) { // function declaration
                Token keyword           = stream.next();
                Token name              = stream.next();

                TokenStream arguments   = getTokensFollowing(LeftParenthesisSymbol, stream, "expected an '(' at line: " + keyword.getLine() + ".");
                TokenStream body        = getTokensFollowing(LeftBraceSymbol, stream, "expected an '{' at line: " + keyword.getLine() + ".");

                Set<PapayaField> parsedArguments        = parseFunctionArguments(arguments);
                Set<PapayaStatement> parsedStatements   = parseFunctionBody(body);
                PapayaFunction function = new PapayaFunction(name.getTokenValue(), parsedArguments, parsedStatements, keyword.getLineInfo());

                structure.addFunction(name.getTokenValue(), function);
            } else if (stream.matches(Identifier, Identifier)) { // field declaration
                Token type              = stream.next();
                Token name              = stream.next();
                PapayaStatement assignment   = null;

                if (stream.matches(AssignmentSymbol)) {
                    Token assignmentOperator = stream.next();
                    TokenStream assignmentTokens = getTokensTilEOL(assignmentOperator.getLine(), stream);
                    assignment = parseRighthand(assignmentTokens);
                }

                PapayaField field = new PapayaField(name.getTokenValue(), type.getTokenValue(), type.getLineInfo(), assignment);

                structure.addField(name.getTokenValue(), field);
            } else {
                throw new WolkenException("cannot parse unknown pattern '" + stream + "' in structure scope.");
            }
        }
    }

    private PapayaStatement parseLefthand(TokenStream stream) throws WolkenException {
        return parse(stream, false);
    }

    private PapayaStatement parseRighthand(TokenStream stream) throws WolkenException {
        return parse(stream, true);
    }

    private PapayaStatement parse(TokenStream stream, boolean righthand) throws WolkenException {
        boolean lefthand = !righthand;

        while (stream.hasNext()) {
            if (lefthand && stream.matches(Identifier, Identifier)) {               // field declaration
                Token type              = stream.next();
                Token name              = stream.next();
                PapayaStatement assignment   = null;

                if (stream.matches(AssignmentSymbol)) {
                    Token assignmentOperator = stream.next();
                    TokenStream assignmentTokens = getTokensTilEOL(assignmentOperator.getLine(), stream);
                    assignment = parseRighthand(assignmentTokens);
                }

                PapayaField field = new PapayaField(name.getTokenValue(), type.getTokenValue(), type.getLineInfo(), assignment);
                return new FieldDeclarationStatement(field, assignment);
            } else if (stream.matches(Identifier, ColonEqualsSymbol)) { // quick field declaration
                Token name                  = stream.next();
                Token assignmentOperator    = stream.next();

                PapayaStatement assignment  = parseRighthand(getTokensTilEOL(assignmentOperator.getLine(), stream));

                if (assignment == null) {
                    throw new WolkenException("expected a valid after ':=' at "+assignmentOperator.getLineInfo()+".");
                }

                PapayaField field = new PapayaField(name.getTokenValue(), "?", name.getLineInfo(), assignment);
                return new FieldDeclarationStatement(field, assignment);
            } else if (stream.matches(Identifier, LeftParenthesisSymbol)) { // call function
                Token name                      = stream.next();
                TokenStream argumentStream      = getTokensFollowing(LeftParenthesisSymbol, stream, "expected an '(' at line: " + name.getLine() + ".");
                Set<PapayaStatement> arguments  = parseFunctionBody(argumentStream);

                return new FunctionCallStatement(name.getTokenValue(), arguments, name.getLineInfo());
            } else if (stream.matches(IncrementSymbol)) {               // prefix ++
            } else if (stream.matches(DecrementSymbol)) {               // prefix --
            } else {
                throw new WolkenException("cannot parse unknown pattern '" + stream + "' in function scope.");
            }
        }

        return null;
    }

    private Set<PapayaStatement> parseFunctionBody(TokenStream stream) throws WolkenException {
        Set<PapayaStatement> statements = new LinkedHashSet<>();

        while (stream.hasNext()) {
            PapayaStatement statement = parseLefthand(stream);
            if (statement != null) {
                statements.add(statement);
            }
        }

        return statements;
    }

    private Set<PapayaField> parseFunctionArguments(TokenStream stream) throws WolkenException {
        boolean hasDefaultValue = false;
        Set<PapayaField> fields = new LinkedHashSet<>();

        while (stream.hasNext()) {
            if (stream.matches(Identifier, Identifier)) { // field declaration
                Token type              = stream.next();
                Token name              = stream.next();
                PapayaStatement assignment   = null;

                if (stream.matches(AssignmentSymbol)) {
                    Token assignmentOperator = stream.next();
                    TokenStream assignmentTokens = getTokensTilEOL(assignmentOperator.getLine(), stream);
                    assignment = parseRighthand(assignmentTokens);
                    hasDefaultValue = true;
                } else {
                    if (hasDefaultValue) {
                        throw new WolkenException("expected value an '=' " + name.getLineInfo() + ".");
                    }
                }

                fields.add(new PapayaField(name.getTokenValue(), type.getTokenValue(), type.getLineInfo(), assignment));
            } else {
                throw new WolkenException("cannot parse unknown pattern '" + stream + "' in function () scope.");
            }
        }

        return fields;
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

    private TokenStream getTokensFollowing(TokenType opener, TokenStream stream, String error) throws WolkenException {
        TokenStream result  = new TokenStream();
        TokenType closer    = None;

        if (!stream.matches(opener)) {
            throw new WolkenException(error);
        }

        // skip the opener symbol.
        stream.next();

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
