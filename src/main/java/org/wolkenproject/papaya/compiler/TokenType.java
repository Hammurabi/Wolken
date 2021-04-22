package org.wolkenproject.papaya.compiler;

public enum TokenType {
    None("None"),
    IntegerNumber("Int"),
    BinaryString("Bin"),
    Base16String("Base16"),
    AsciiChar("AsciiChar"),
    DecimalNumber("Decimal"),
    AsciiString("Ascii"),
    ByteString(""),
    GenericAddress(""),
    ContractAddress(""),
    Identifier("Ident"),
    ModifierKeyword("AccessModifier"),

    LogicalNotSymbol,
    AssignmentSymbol,

    AddSymbol,
    SubSymbol,
    MulSymbol,
    DivSymbol,
    ModSymbol,
    PowSymbol,

    XorSymbol,
    AndSymbol,
    OrSymbol,

    LogicalNotEqualsSymbol,
    EqualsSymbol,

    AddEqualsSymbol,
    SubEqualsSymbol,
    MulEqualsSymbol,
    DivEqualsSymbol,
    ModEqualsSymbol,
    PowEqualsSymbol,

    XorEqualsSymbol,
    AndEqualsSymbol,
    OrEqualsSymbol,

    NotSymbol,

    LogicalAndSymbol,
    LogicalOrSymbol,

    LogicalAndEqualsSymbol,
    LogicalOrEqualsSymbol,

    UnsignedRightShiftSymbol,
    RightShiftSymbol,
    LeftShiftSymbol,

    MemberAccessSymbol,
    StaticMemberAccessSymbol,
    LambdaSymbol,
    DoubleDotSymbol,
    CommaSymbol,
    HashTagSymbol,
    SemiColonSymbol,
    LessThanSymbol,
    GreaterThanSymbol,
    LessThanEqualsSymbol,
    GreaterThanEqualsSymbol,

    LeftParenthesisSymbol,
    RightParenthesisSymbol,
    LeftBracketSymbol,
    RightBracketSymbol,
    LeftBraceSymbol,
    RightBraceSymbol,

    ForKeyword,
    WhileKeyword,

    BreakKeyword,
    ContinueKeyword,
    PassKeyword,
    ReturnKeyword,

    FunctionKeyword,
    ContractKeyword,
    ModuleKeyword,

    ClassKeyword,
    StructKeyword,
    ExtendsKeyword,
    ImplementsKeyword,

    IncrementSymbol,
    DecrementSymbol,
    ColonEqualsSymbol,




    Parenthesis,
    Braces,
    Brackets,

    FieldDeclaration,
    AssignmentStatement,
    FunctionCall,
    FunctionArguments,
    FunctionBody,
    RootToken,
    Structure,

    Return,
    ;

    private final String string;

    public TokenType(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
