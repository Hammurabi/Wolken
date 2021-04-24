package org.wolkenproject.papaya.parser;

public class ParseResult implements Comparable<ParseResult> {
    private final float percentParsed;
    private final Subrule subrule;
    private final ParseToken token;

    public ParseResult(float percentParsed, Subrule subrule, ParseToken token) {
        this.percentParsed = percentParsed;
        this.subrule = subrule;
        this.token = token;
    }

    @Override
    public int compareTo(ParseResult o) {
        return percentParsed > o.percentParsed ? -1 : 1;
    }

    public Subrule getSubrule() {
        return subrule;
    }

    public ParseToken getToken() {
        return token;
    }

    public boolean isParsed() {
        return token != null;
    }
}
