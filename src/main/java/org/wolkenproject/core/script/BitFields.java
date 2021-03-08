package org.wolkenproject.core.script;

import org.wolkenproject.utils.BitInputStream;

import java.util.List;

public class BitFields {
    private List<BitField>                  fields;

    public BitFields addField(int length, String name) {
        return addField(length, name, null);
    }

    public BitFields addField(int length, String name, BitCondition ...conditions) {
        BitField bitField = new BitField();
        bitField.name   = name;
        bitField.length = length;
        bitField.conditions = conditions;

        fields.add(bitField);

        return this;
    }

    public OpArgs getArguments(BitInputStream inputStream) {
        return null;
    }

    public static class BitField {
        private String  name;
        private int     length;
        private BitCondition conditions[];

        public byte[] getValue(BitInputStream inputStream) {
            return inputStream.getBitsAsByteArray(length);
        }
    }

    public static interface BitCondition {
        public boolean get(int value);
    }
}
