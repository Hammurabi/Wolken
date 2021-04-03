package org.wolkenproject.papaya.compiler;

import org.wolkenproject.exceptions.PapayaException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.wolkenproject.papaya.compiler.TokenType.*;

public class PapayaParser {
    public PapayaApplication ingest(TokenStream stream) throws PapayaException {
        return parseABI(stream);
    }

//    private Token makeAST(TokenStream stream) throws PapayaException {
//        Token root = new Token("", RootToken, new LineInfo(0, 0));
//
//        while (stream.hasNext()) {
//            // contract, class, or struct declaration.
//            if (stream.matches(ContractKeyword, Identifier) || stream.matches(ClassKeyword, Identifier) || stream.matches(StructKeyword, Identifier)) {
//                Token keyword   = stream.next();
//                Token name      = stream.next();
//
//                // check for inheritance.
//                if (stream.matches(ExtendsKeyword) || stream.matches(ImplementsKeyword)) {
//                    throw new PapayaException("inheritance is not allowed.");
//                }
//
//                // get a new token stream containing all the body tokens.
//                TokenStream body= getTokensFollowing(LeftBraceSymbol, stream, "expected an '{' at line: " + keyword.getLine() + ".");
//
//                // create a structure.
//                Token structure = new Token("", Structure, keyword.getLineInfo());
//
//                structure.add(keyword);
//                structure.add(name);
//
//                // parse the body into the structure.
//                parseStructure(structure, body);
//
//                // add the structure to the root
//                root.add(structure);
//            } else {
//                throw new PapayaException("cannot parse unknown pattern '" + stream + "' in global scope.");
//            }
//        }
//
//        return null;
//    }

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
                PapayaStructure structure = new PapayaStructure(name.getTokenValue(), type, keyword.getLineInfo());

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
                List<Token> parsedStatements            = fullParse(body, false);
                PapayaFunction function                 = new PapayaFunction(modifier, name.getTokenValue(), parsedArguments, parsedStatements, keyword.getLineInfo());
                modifier = AccessModifier.None;

                structure.addFunction(name.getTokenValue(), function);
            } else if (stream.matches(Identifier, Identifier)) { // field declaration
                Token type              = stream.next();
                Token name              = stream.next();
                Token assignment        = null;

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

    private Token parseLefthand(TokenStream stream) throws PapayaException {
        return parse(stream, false);
    }

    private Token parseRighthand(TokenStream stream) throws PapayaException {
        return parse(stream, true);
    }

    private Token parse(TokenStream stream, boolean righthand) throws PapayaException {
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

            if (lefthand && (stream.matches(PassKeyword) || stream.matches(ContinueKeyword) || stream.matches(BreakKeyword))) {
                return stream.next();
            } else if (lefthand && stream.matches(ReturnKeyword)) {
                Token keyword           = stream.next();
                TokenStream returnTokens= getTokensTilEOL(keyword.getLine(), stream);
                List<Token> tokens = fullParse(returnTokens, true);

                if (tokens.isEmpty()) {
                    throw new PapayaException("expected a value after 'return' keyword at "+keyword.getLineInfo()+", to return without a value use 'pass' keyword.");
                }

                keyword.addChildren(tokens);

                return keyword;
            } else if (lefthand && (stream.matches(ForKeyword) || stream.matches(WhileKeyword))) {
                Token keyword           = stream.next();
                String name             = keyword.getTokenValue();

                TokenStream condition   = getTokensFollowing(LeftParenthesisSymbol, stream, "expected an '(' at line: " + keyword.getLine() + ".");
                TokenStream bode        = getTokensFollowing(LeftBraceSymbol, stream, "expected an '{' at line: " + keyword.getLine() + ".");

                List<Token> tokens      = fullParse(condition, false);
                List<Token> bodetk      = fullParse(bode, false);

                if (tokens.isEmpty()) {
                    throw new PapayaException("expected a condition in parenthesis '"+name+" (..)' at "+keyword.getLineInfo()+", to return without a value use 'pass' keyword.");
                }

                Token cond = new Token("", FunctionArguments, keyword.getLineInfo());
                Token body = new Token("", FunctionBody, keyword.getLineInfo());
                cond.addChildren(tokens);
                body.addChildren(bodetk);

                keyword.add(cond);
                keyword.add(body);

                return keyword;
            } else if (lefthand && stream.matches(Identifier, Identifier)) { // field declaration
                Token type              = stream.next();
                Token name              = stream.next();
                Token assignment        = null;

                if (stream.matches(AssignmentSymbol)) {
                    Token assignmentOperator = stream.next();
                    TokenStream assignmentTokens = getTokensTilEOL(assignmentOperator.getLine(), stream);
                    List<Token> tokens = fullParse(assignmentTokens, true);
                    if (tokens != null && !tokens.isEmpty()) {
                        assignment = new Token("", AssignmentStatement, assignmentOperator.getLineInfo());
                    }
                }

                Token declaration = new Token("", FieldDeclaration, type.getLineInfo());
                Token mod = new Token(modifier.name(), ModifierKeyword, type.getLineInfo());
                declaration.add(type);
                declaration.add(name);
                declaration.add(mod);
                if (assignment != null) {
                    declaration.add(assignment);
                }

                modifier = AccessModifier.None;

                return declaration;
            } else if (lefthand && stream.matches(Identifier, ColonEqualsSymbol)) { // quick field declaration
                Token name                  = stream.next();
                Token assignmentOperator    = stream.next();

                Token assignment            = parseRighthand(getTokensTilEOL(assignmentOperator.getLine(), stream));

                if (assignment == null) {
                    throw new PapayaException("expected a value after ':=' at "+assignmentOperator.getLineInfo()+".");
                }

                Token declaration = new Token("", FieldDeclaration, name.getLineInfo());
                Token mod = new Token(modifier.name(), ModifierKeyword, name.getLineInfo());
                declaration.add(new Token("?", Identifier, name.getLineInfo()));
                declaration.add(name);
                declaration.add(mod);
                declaration.add(assignment);

                modifier = AccessModifier.None;
                return declaration;
            } else if (stream.matches(Identifier, LeftParenthesisSymbol)) { // call function
                Token name                      = stream.next();
                TokenStream argumentStream      = getTokensFollowing(LeftParenthesisSymbol, stream, "expected an '(' at line: " + name.getLine() + ".");

                Token arguments                 = new Token("", FunctionArguments, name.getLineInfo());
                if (argumentStream.isEmpty()) {
                    List<Token> args            = fullParse(argumentStream, true);
                    arguments.addChildren(args);
                }

                Token call = new Token("", FunctionCall, name.getLineInfo());
                call.add(name);
                call.add(arguments);

                return call;
            } else if (stream.matches(Identifier, AssignmentSymbol)) { // assignment of value
                Token name                  = stream.next();
                Token assignmentOperator    = stream.next();
                TokenStream eol             = getTokensTilEOL(assignmentOperator.getLine(), stream);

                if (eol.isEmpty()) {
                    throw new PapayaException("expected a value after ':=' at "+assignmentOperator.getLineInfo()+".");
                }

                List<Token> parsed          = fullParse(eol, true);

                if (parsed.isEmpty()) {
                    throw new PapayaException("expected a value after ':=' at "+assignmentOperator.getLineInfo()+".");
                }

                Token assignmentToken = new Token("", AssignmentStatement, name.getLineInfo());
                assignmentToken.add(name);
                assignmentToken.addChildren(parsed);

                return assignmentToken;
            } else if (stream.matches(Identifier)) { // identifier
                return stream.next();
            } else if (stream.matches(LeftParenthesisSymbol)) { // (
                Token token = new Token("", Parenthesis, stream.peek().getLineInfo());

                TokenStream parenthesis = getTokensFollowing(LeftParenthesisSymbol, stream, "expected an '(' at line: " + stream.peek().getLine() + ".");
                if (parenthesis.isEmpty()) {
                    throw new PapayaException("expected a value inside '()' at line: " + stream.peek().getLine() + ".");
                }

                token.addChildren(fullParse(parenthesis, righthand));

                return token;
            } else if (stream.matches(LeftBraceSymbol)) { // {
                Token token = new Token("", Braces, stream.peek().getLineInfo());

                TokenStream parenthesis = getTokensFollowing(LeftBraceSymbol, stream, "expected an '{' at line: " + stream.peek().getLine() + ".");
                token.addChildren(fullParse(parenthesis, righthand));

                return token;
            } else if (stream.matches(LeftBracketSymbol)) { // [
                Token token = new Token("", Brackets, stream.peek().getLineInfo());

                TokenStream parenthesis = getTokensFollowing(LeftBracketSymbol, stream, "expected an '[' at line: " + stream.peek().getLine() + ".");
                token.addChildren(fullParse(parenthesis, righthand));

                return token;
            } else if (
                    stream.matches(IncrementSymbol) ||
                    stream.matches(DecrementSymbol) ||
                    stream.matches(AddSymbol) ||
                    stream.matches(SubSymbol) ||
                    stream.matches(MulSymbol) ||
                    stream.matches(DivSymbol) ||
                    stream.matches(ModSymbol) ||
                    stream.matches(PowSymbol) ||
                    stream.matches(AndSymbol) ||
                    stream.matches(OrSymbol) ||
                    stream.matches(XorSymbol) ||

                    stream.matches(LogicalNotEqualsSymbol) ||
                    stream.matches(EqualsSymbol) ||
                    stream.matches(AddEqualsSymbol) ||
                    stream.matches(SubEqualsSymbol) ||
                    stream.matches(MulEqualsSymbol) ||
                    stream.matches(DivEqualsSymbol) ||
                    stream.matches(ModEqualsSymbol) ||
                    stream.matches(PowEqualsSymbol) ||
                    stream.matches(XorEqualsSymbol) ||
                    stream.matches(AndEqualsSymbol) ||
                    stream.matches(OrEqualsSymbol) ||
                    stream.matches(NotSymbol) ||
                    stream.matches(LogicalAndSymbol) ||
                    stream.matches(LogicalOrSymbol) ||
                    stream.matches(LogicalAndEqualsSymbol) ||
                    stream.matches(LogicalOrEqualsSymbol) ||
                    stream.matches(UnsignedRightShiftSymbol) ||
                    stream.matches(RightShiftSymbol) ||
                    stream.matches(LeftShiftSymbol) ||
                    stream.matches(MemberAccessSymbol) ||
                    stream.matches(StaticMemberAccessSymbol) ||
                    stream.matches(LambdaSymbol) ||
                    stream.matches(DoubleDotSymbol) ||
                    stream.matches(CommaSymbol) ||
                    stream.matches(HashTagSymbol) ||
                    stream.matches(SemiColonSymbol) ||
                    stream.matches(LessThanSymbol) ||
                    stream.matches(GreaterThanSymbol) ||
                    stream.matches(LessThanEqualsSymbol) ||
                    stream.matches(GreaterThanEqualsSymbol)
            ) {
                return stream.next();
            } {} else {
                throw new PapayaException("cannot parse unknown pattern '" + stream + "' in function scope.");
            }
        }

        return null;
    }

    private List<Token> fullParse(TokenStream stream, boolean righthand) throws PapayaException {
        List<Token> firstRound = new ArrayList<>();
        while (stream.hasNext()) {
            firstRound.add(parse(stream, righthand));
        }

        return firstRound;
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
                Token assignment        = null;

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
