package org.wolkenproject.core.papaya.compiler;

public class LineInfo {
    private final int line;
    private final int offset;

    public LineInfo(int line, int offset) {
        this.line = line;
        this.offset = offset;
    }
}
