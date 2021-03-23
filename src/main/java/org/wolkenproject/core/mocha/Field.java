package org.wolkenproject.core.mocha;

public class Field {
    public String getName() {
        return name;
    }

    public int getVisibility() {
        return visibility;
    }

    public static final class Visibility {
        public static final int
                Private     = 0,
                Public      = 1,
                Protected   = 2;
    }

    private String  name;
    private int     visibility;

    public Field(String name, int visibility) {
        this.name       = name;
        this.visibility = visibility;
    }
}
