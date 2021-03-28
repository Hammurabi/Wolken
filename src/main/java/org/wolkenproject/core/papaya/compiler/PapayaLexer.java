package org.wolkenproject.core.papaya.compiler;

public class PapayaLexer {
    public TokenStream ingest(String data) {
        TokenStream tokenStream = new TokenStream();

        StringBuilder   builder = new StringBuilder();
        char            lastChar= '\0';
        int             line    = 1;
        int             offset  = 0;

        for (char character : data.toCharArray()) {
            offset ++;

            if (character == '\n') {
                if (builder.length() != 0) {
                    tokenStream.add(builder, line, offset);
                    builder = new StringBuilder();
                }

                offset = 1;
            }

            if (character == ' ') {
                if (builder.length() != 0) {
                    tokenStream.add(builder, line, offset);
                    builder = new StringBuilder();
                }
            }
        }
    }
}
