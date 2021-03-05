package org.wolkenproject.core.script;

import org.wolkenproject.utils.Tuple;

import java.util.List;

public class BitFields {
    private List<Tuple<Integer, String>>    fields;
    private int                             totalBits;
    private int                             conditionalBits;

    public int getTotalBits() {
        return totalBits;
    }
    public int getConditionalBits() {
        return conditionalBits;
    }

    public int getTotalBytes() {
        return (int) Math.ceil(totalBits / 8.0);
    }

    public BitFields addField(int length, String name) {
        totalBits += length;
        fields.add(new Tuple<>(totalBits, name));

        return this;
    }

    public BitFields addCond(int length, BitCondition condition, String name) {
        conditionalBits += length;
        fields.add(new Tuple<>(totalBits, name));

        return this;
    }

    public static class BitField {
    }

    private static interface BitCondition {
        public boolean get(int index, BitField previous, BitField next, BitFields self);
    }
}
