package org.wolkenproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wolkenproject.papaya.compiler.PapayaLexer;

public class PapayaTests {
    @Test
    public void testLexer() {
        PapayaLexer papayaLexer = new PapayaLexer(PapayaLexer.getTokenTypes());
    }
}
