package org.wolkenproject.core.papaya.compiler;

public enum TokenType {
    None,
    IntegerNumber,
    BinaryString,
    Base16String,
    DecimalNumber,
    AsciiString,
    ByteString,
    GenericAddress,
    ContractAddress,
    Identifier,

    LogicalNotSymbol,
    AssignmentSymbol,
    EqualsSymbol,

    AddSymbol,
    SubSymbol,
    MulSymbol,
    DivSymbol,
    ModSymbol,

    XorSymbol,
    AndSymbol,
    OrSymbol,

    NotSymbol,
}
