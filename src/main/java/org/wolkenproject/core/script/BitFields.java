package org.wolkenproject.core.script;

import org.wolkenproject.utils.Tuple;

import java.util.List;

public class BitFields {
    private List<Tuple<Integer, String>>    fields;
    private int                             totalBits;

    public int getTotalBits() {
        return totalBits;
    }

    public int getTotalBytes() {
        return (int) Math.ceil(totalBits / 8.0);
    }

    public BitFields addField(int length, String name) {
        totalBits += length;
        fields.add(new Tuple<>(totalBits, name));

        return this;
    }

    public BitFields addVariableField(int length, int maxLength, String name) {
        totalBits += length;
        fields.add(new Tuple<>(totalBits, name));

        return this;
    }
}
