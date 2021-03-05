package org.wolkenproject.core.script;

import java.util.List;

public class BitFields {
    private List<Integer>   fields;
    private int             totalBits;

    public int getTotalBits() {
        return totalBits;
    }

    public int getTotalBytes() {
        return (int) Math.ceil(totalBits / 8.0);
    }

    public BitFields addField(int length) {
        totalBits += length;
        fields.add(totalBits);

        return this;
    }
}
