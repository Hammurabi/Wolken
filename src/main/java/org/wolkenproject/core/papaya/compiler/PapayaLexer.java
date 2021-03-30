package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.exceptions.WolkenException;

import java.util.*;

public class PapayaLexer {
    private static final char EOF = '\0';
    private static final char END = '\n';
    private static final char WTS = ' ';
    private static final char TAB = '\t';

    private final Map<String, TokenType>    typeMap;
    private final Set<Character>            symbolSet;
    private final Set<Character>            stackableSymbolSet;


    public PapayaLexer(Map<String, TokenType> typeMap) {
        this.typeMap = typeMap;
        this.symbolSet = new HashSet<>();
        this.stackableSymbolSet = new HashSet<>();
        symbolSet.add('+');
        symbolSet.add('-');
        symbolSet.add('*');
        symbolSet.add('/');
        symbolSet.add('=');
        symbolSet.add('!');
        symbolSet.add('%');
        symbolSet.add('^');
        symbolSet.add('&');
        symbolSet.add('<');
        symbolSet.add('>');
        symbolSet.add('|');
        symbolSet.add('.');
        symbolSet.add(':');
        symbolSet.add('\\');
        stackableSymbolSet.addAll(symbolSet);

        symbolSet.add('~');
        symbolSet.add(',');
        symbolSet.add('$');
        symbolSet.add('#');
        symbolSet.add('@');
        symbolSet.add('?');
        symbolSet.add('(');
        symbolSet.add(')');
        symbolSet.add('[');
        symbolSet.add(']');
        symbolSet.add('{');
        symbolSet.add('}');
    }

    public TokenStream ingest(String program) throws WolkenException {
        List<TokenBuilder> builderList = new ArrayList<>();
        TokenStream tokenStream = new TokenStream();

        StringBuilder   builder = new StringBuilder();
        char            lastChar= '\0';

        boolean isString = false;
        int line = 1;
        int offset = 0;
        int whitespace = 0;

        TokenBuilder token = null;
        TokenBuilder prevs = null;

        for (int i = 0; i < program.length(); i ++) {

            char current = program.charAt(i);
            char last = i > 0 ? program.charAt(i - 1) : '\0';
            char next = i < program.length() - 1 ? program.charAt(i + 1) : '\0';

            if (current == END)
            {
                builderList.add(token);
                builderList.add(new TokenBuilder());
                prevs = token;
                token = null;
                offset = 0;
                whitespace = 0;
                line++;
                continue;
            } else if (current == WTS && !(token != null && (token.toString().startsWith("\"") || token.toString().startsWith("\'"))))
            {
                whitespace++;
                builderList.add(token);
                offset ++;
                prevs = token;
                token = null;
                continue;
            } else if (current == TAB && !(token != null && (token.toString().startsWith("\"") || token.toString().startsWith("\'"))))
            {
                whitespace += 4;
                offset += 4;
                builderList.add(token);
                prevs = token;
                token = null;
                continue;
            }

            boolean separator   = symbolSet.contains(current);
            boolean wasnull     = token == null;

            if (token == null) {
                token = new TokenBuilder("", line, offset, whitespace);
            }

            isString = (token.toString().startsWith("\"") || token.toString().startsWith("\'"));

            if (isString || (wasnull && (current == '"' || current == '\'')))
            {
                if (current == '"' || current == '\'' || separator)
                {
                    if (last == '\\') token.append(current);
                    else if (token.toString().startsWith(current + ""))
                    {
                        token.append(current);
                        builderList.add(token);
                        prevs = token;
                        token = null;
                    } else token.append(current);
                } else token.append(current);

                offset++;
                continue;
            } else if (separator)
            {
                builderList.add(token);
                token = new TokenBuilder("" + current, line, offset, whitespace);
                builderList.add(token);
                prevs = token;
                token = null;
            } else token.append(current);

            offset++;
        }

        Iterator<TokenBuilder> iterator = builderList.iterator();
        while (iterator.hasNext()) {
            TokenBuilder tokenBuilder = nextBuilder(iterator);
            if (tokenBuilder == null) {
                continue;
            }

            if (tokenBuilder.isSymbol(stackableSymbolSet)) {
                while (iterator.hasNext()) {
                    TokenBuilder toke = nextBuilder(iterator);
                    if (toke != null) {
                        if (!toke.isSymbol(stackableSymbolSet)) {
                            break;
                        }

                        tokenBuilder.append(toke.toString());
                    }
                }
            }

            tokenStream.add(getToken(tokenBuilder.toString(), tokenBuilder.getLine(), tokenBuilder.getOffset(), typeMap));
        }

        return tokenStream;
    }

    private static TokenBuilder nextBuilder(Iterator<TokenBuilder> iterator) {
        while (iterator.hasNext()) {
            TokenBuilder tokenBuilder = iterator.next();
            if (tokenBuilder == null) {
                continue;
            }

            if (tokenBuilder.isEmpty()) {
                continue;
            }

            return tokenBuilder;
        }

        return null;
    }

    private String escape(char character) throws WolkenException {
        switch (character) {
            case 'n':
                return "\n";
            case 't':
                return "\t";
            case '0':
                return "\0";
            case '\'':
                return "'";
            case '"':
                return "\"";
            default:
                throw new WolkenException("escaping invalid character '" + character + "'.");
        }
    }

    private boolean isSymbol(char character) {
        return symbolSet.contains(character);
    }

    private boolean isString(String string) {
        return string.startsWith("'") || string.startsWith("\"");
    }

    private static Token getToken(String string, int line, int offset, Map<String, TokenType> typeMap) throws WolkenException {
        if ((string.startsWith("'") && string.endsWith("'")) || (string.startsWith("\"") && string.endsWith("\""))) {
            String str = "";
            if (string.length() > 2) {
                str = string.substring(1, string.length() - 1);
            }

            return new Token(str, TokenType.AsciiString, line, offset);
        }

        for (String regex : typeMap.keySet()) {
            if (string.matches(regex)) {
                return new Token(string, typeMap.get(regex), line, offset);
            }
        }

        throw new WolkenException("could not create token for string '" + string + "'" + "at line: " + line + " offset: " + offset + ".");
    }

    public static Map<String, TokenType> getTokenTypes() {
        Map<String, TokenType> tokenType = new LinkedHashMap<>();

        // keywords
        tokenType.put("for", TokenType.ForKeyword);
        tokenType.put("while", TokenType.WhileKeyword);
        tokenType.put("break", TokenType.BreakKeyword);
        tokenType.put("continue", TokenType.ContinueKeyword);
        tokenType.put("pass", TokenType.PassKeyword);
        tokenType.put("return", TokenType.ReturnKeyword);
        tokenType.put("fn", TokenType.FunctionKeyword);
        tokenType.put("module", TokenType.ModuleKeyword);
        tokenType.put("contract", TokenType.ContractKeyword);
        tokenType.put("class", TokenType.ClassKeyword);
        tokenType.put("struct", TokenType.StructKeyword);
        tokenType.put("extends", TokenType.ExtendsKeyword);
        tokenType.put("implements", TokenType.ImplementsKeyword);
        tokenType.put("and", TokenType.LogicalAndSymbol);
        tokenType.put("or", TokenType.LogicalOrSymbol);

        tokenType.put("public", TokenType.ModifierKeyword);
        tokenType.put("private", TokenType.ModifierKeyword);
        tokenType.put("protected", TokenType.ModifierKeyword);
        tokenType.put("readonly", TokenType.ModifierKeyword);

        // other
        tokenType.put("\\d+", TokenType.IntegerNumber);
        tokenType.put("[0][b][0-1]+", TokenType.IntegerNumber);
        tokenType.put("0x[\\d|(a|b|c|d|e|f|A|B|C|D|E|F)]+", TokenType.Base16String);
        tokenType.put("\\\\w", TokenType.AsciiChar);
        tokenType.put("\\d+\\.\\d+", TokenType.DecimalNumber);
        tokenType.put("\\d+\\.", TokenType.DecimalNumber);
        tokenType.put("\\.\\d+", TokenType.DecimalNumber);

        tokenType.put("\\+\\+", TokenType.IncrementSymbol);
        tokenType.put("\\-\\-", TokenType.DecrementSymbol);

        tokenType.put("\\!", TokenType.LogicalNotSymbol);
        tokenType.put("\\=", TokenType.AssignmentSymbol);
        tokenType.put("\\+", TokenType.AddSymbol);
        tokenType.put("\\-", TokenType.SubSymbol);
        tokenType.put("\\*", TokenType.MulSymbol);
        tokenType.put("\\/", TokenType.DivSymbol);
        tokenType.put("\\%", TokenType.ModSymbol);
        tokenType.put("\\*\\*", TokenType.PowSymbol);
        tokenType.put("^", TokenType.XorSymbol);
        tokenType.put("&", TokenType.AndSymbol);
        tokenType.put("|", TokenType.OrSymbol);

        tokenType.put("\\!\\=", TokenType.LogicalNotEqualsSymbol);
        tokenType.put("\\=\\=", TokenType.EqualsSymbol);
        tokenType.put("\\+\\=", TokenType.AddEqualsSymbol);
        tokenType.put("\\-\\=", TokenType.SubEqualsSymbol);
        tokenType.put("\\*\\=", TokenType.MulEqualsSymbol);
        tokenType.put("\\/\\=", TokenType.DivEqualsSymbol);
        tokenType.put("\\%\\=", TokenType.ModEqualsSymbol);
        tokenType.put("\\*\\*\\=", TokenType.PowEqualsSymbol);
        tokenType.put("^\\=", TokenType.XorEqualsSymbol);
        tokenType.put("&\\=", TokenType.AndEqualsSymbol);
        tokenType.put("|\\=", TokenType.OrEqualsSymbol);

        tokenType.put("~", TokenType.NotSymbol);
        tokenType.put("\\&\\&", TokenType.LogicalAndSymbol);
        tokenType.put("\\&\\&\\=", TokenType.LogicalAndEqualsSymbol);
        tokenType.put("\\|\\|", TokenType.LogicalOrSymbol);
        tokenType.put("\\|\\|\\=", TokenType.LogicalOrEqualsSymbol);
        tokenType.put("\\>\\>\\>", TokenType.UnsignedRightShiftSymbol);
        tokenType.put("\\>\\>", TokenType.RightShiftSymbol);
        tokenType.put("\\<\\<", TokenType.LeftShiftSymbol);

        tokenType.put("\\.", TokenType.MemberAccessSymbol);
        tokenType.put("\\.\\.", TokenType.DoubleDotSymbol);
        tokenType.put("\\,", TokenType.CommaSymbol);
        tokenType.put("\\#", TokenType.HashTagSymbol);
        tokenType.put("\\;", TokenType.SemiColonSymbol);
        tokenType.put("\\:\\:", TokenType.StaticMemberAccessSymbol);
        tokenType.put("\\:\\=", TokenType.ColonEqualsSymbol);
        tokenType.put("\\=\\>", TokenType.LambdaSymbol);

        tokenType.put("\\<", TokenType.LessThanSymbol);
        tokenType.put("\\>", TokenType.GreaterThanSymbol);

        tokenType.put("\\<\\=", TokenType.LessThanEqualsSymbol);
        tokenType.put("\\>\\=", TokenType.GreaterThanEqualsSymbol);

        tokenType.put("\\(", TokenType.LeftParenthesisSymbol);
        tokenType.put("\\)", TokenType.RightParenthesisSymbol);

        tokenType.put("\\[", TokenType.LeftBracketSymbol);
        tokenType.put("\\]", TokenType.RightBracketSymbol);

        tokenType.put("\\{", TokenType.LeftBraceSymbol);
        tokenType.put("\\}", TokenType.RightBraceSymbol);

        // we leave the identifier regex for the end.
        tokenType.put("([A-z]|\\_)+\\d*", TokenType.Identifier);


//        tokenType.put("N([A-z]|[1|2|3|4|5|6|7|8|9])+", TokenType.Base58String);

        return tokenType;
    }
}
