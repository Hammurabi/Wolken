package org.wolkenproject.papaya.compiler;

import org.wolkenproject.papaya.compiler.statements.FieldDeclarationStatement;
import org.wolkenproject.papaya.compiler.statements.FunctionCallStatement;
import org.wolkenproject.papaya.compiler.statements.VariableAssignment;
import org.wolkenproject.exceptions.PapayaException;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.wolkenproject.papaya.compiler.TokenType.*;

public class PapayaParser {
    public PapayaApplication ingest(TokenStream stream) throws PapayaException {
        return parseABI(stream);
    }

    private PapayaApplication parseABI(TokenStream stream) throws PapayaException {
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
                    throw new PapayaException("inheritance is not allowed.");
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
                throw new PapayaException("cannot parse unknown pattern '" + stream + "' in global scope.");
            }
        }

        return null;
    }

    private void parseStructure(PapayaStructure structure, TokenStream stream) throws PapayaException {
        AccessModifier modifier = AccessModifier.None;

        while (stream.hasNext()) {
            if (stream.matches(ModifierKeyword)) {
                Token next = stream.next();
                if (modifier != AccessModifier.None) {
                    throw new PapayaException("modifier already set to '" + modifier + "' at " + next.getLineInfo() + ".");
                }

                modifier = AccessModifier.valueOf(next.getTokenValue());
            }

            if (stream.matches(FunctionKeyword, Identifier, LeftParenthesisSymbol)) { // function declaration
                Token keyword           = stream.next();
                Token name              = stream.next();

                TokenStream arguments   = getTokensFollowing(LeftParenthesisSymbol, stream, "expected an '(' at line: " + keyword.getLine() + ".");
                TokenStream body        = getTokensFollowing(LeftBraceSymbol, stream, "expected an '{' at line: " + keyword.getLine() + ".");

                Set<PapayaField> parsedArguments        = parseFunctionArguments(arguments);
                PapayaStatement parsedStatements        = parseFunctionBody(body);
                PapayaFunction function                 = new PapayaFunction(modifier, name.getTokenValue(), parsedArguments, parsedStatements, keyword.getLineInfo());
                modifier = AccessModifier.None;

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

                PapayaField field = new PapayaField(modifier, name.getTokenValue(), type.getTokenValue(), type.getLineInfo(), assignment);
                modifier = AccessModifier.None;

                structure.addField(name.getTokenValue(), field);
            } else {
                throw new PapayaException("cannot parse unknown pattern '" + stream + "' in structure scope.");
            }
        }
    }

    private PapayaStatement parseLefthand(TokenStream stream) throws PapayaException {
        return parse(stream, false);
    }

    private PapayaStatement parseRighthand(TokenStream stream) throws PapayaException {
        return parse(stream, true);
    }

    private PapayaStatement parse(TokenStream stream, boolean righthand) throws PapayaException {
        boolean lefthand = !righthand;
        AccessModifier modifier = AccessModifier.None;

        while (stream.hasNext()) {
            if (lefthand && stream.matches(ModifierKeyword)) {
                Token next = stream.next();
                if (modifier != AccessModifier.None) {
                    throw new PapayaException("modifier already set to '" + modifier + "' at " + next.getLineInfo() + ".");
                }

                modifier = AccessModifier.valueOf(next.getTokenValue());
            }

            if (lefthand && stream.matches(Identifier, Identifier)) { // field declaration
                Token type              = stream.next();
                Token name              = stream.next();
                PapayaStatement assignment   = null;

                if (stream.matches(AssignmentSymbol)) {
                    Token assignmentOperator = stream.next();
                    TokenStream assignmentTokens = getTokensTilEOL(assignmentOperator.getLine(), stream);
                    assignment = parseRighthand(assignmentTokens);
                }

                PapayaField field = new PapayaField(modifier, name.getTokenValue(), type.getTokenValue(), type.getLineInfo(), assignment);
                modifier = AccessModifier.None;
                return new FieldDeclarationStatement(field, assignment);
            } else if (stream.matches(Identifier, ColonEqualsSymbol)) { // quick field declaration
                Token name                  = stream.next();
                Token assignmentOperator    = stream.next();

                PapayaStatement assignment  = parseRighthand(getTokensTilEOL(assignmentOperator.getLine(), stream));

                if (assignment == null) {
                    throw new PapayaException("expected a valid after ':=' at "+assignmentOperator.getLineInfo()+".");
                }

                PapayaField field = new PapayaField(modifier, name.getTokenValue(), "?", name.getLineInfo(), assignment);
                modifier = AccessModifier.None;
                return new FieldDeclarationStatement(field, assignment);
            } else if (stream.matches(Identifier, LeftParenthesisSymbol)) { // call function
                Token name                      = stream.next();
                TokenStream argumentStream      = getTokensFollowing(LeftParenthesisSymbol, stream, "expected an '(' at line: " + name.getLine() + ".");
                PapayaStatement arguments       = parseFunctionBody(argumentStream);

                return new FunctionCallStatement(name.getTokenValue(), arguments, name.getLineInfo());
            } else if (stream.matches(Identifier, AssignmentSymbol)) { // quick field declaration
                Token name                  = stream.next();
                Token assignmentOperator    = stream.next();
                PapayaStatement assignment  = parseRighthand(getTokensTilEOL(assignmentOperator.getLine(), stream));

                if (assignment == null) {
                    throw new PapayaException("expected a valid after ':=' at "+assignmentOperator.getLineInfo()+".");
                }

                return new VariableAssignment(name, assignment, name.getLineInfo());
            } else if (stream.matches(IncrementSymbol)) {
            } else if (stream.matches(DecrementSymbol)) {
            } else {
                throw new PapayaException("cannot parse unknown pattern '" + stream + "' in function scope.");
            }
        }

        return null;
    }

    private PapayaStatement parseFunctionBody(TokenStream stream) throws PapayaException {
        PapayaStatement statements = new PapayaStatement(new LineInfo(-1, -1));

        while (stream.hasNext()) {
            PapayaStatement statement = parseLefthand(stream);
            if (statement != null) {
                statements.addChild(statement);
            }
        }

        return statements;
    }

    private Set<PapayaField> parseFunctionArguments(TokenStream stream) throws PapayaException {
        boolean hasDefaultValue = false;
        Set<PapayaField> fields = new LinkedHashSet<>();
        AccessModifier modifier = AccessModifier.None;

        while (stream.hasNext()) {
            if (stream.matches(ModifierKeyword)) {
                Token next = stream.next();
                if (modifier != AccessModifier.None) {
                    throw new PapayaException("modifier already set to '" + modifier + "' at " + next.getLineInfo() + ".");
                }

                modifier = AccessModifier.valueOf(next.getTokenValue());
            }


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
                        throw new PapayaException("expected value an '=' " + name.getLineInfo() + ".");
                    }
                }

                fields.add(new PapayaField(modifier, name.getTokenValue(), type.getTokenValue(), type.getLineInfo(), assignment));
                modifier = AccessModifier.None;
            } else {
                throw new PapayaException("cannot parse unknown pattern '" + stream + "' in function () scope.");
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

    private TokenStream getTokensFollowing(TokenType opener, TokenStream stream, String error) throws PapayaException {
        TokenStream result  = new TokenStream();
        TokenType closer    = None;

        if (!stream.matches(opener)) {
            throw new PapayaException(error);
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
                throw new PapayaException("invalid usage of token '" + opener + "' is never closed.");
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

        throw new PapayaException("token '" + opener + "' is never closed.");
    }
}
