package org.wolkenproject.core.script;

import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.Tuple;

import java.util.List;

public class BitFields {
    private List<BitField>                  fields;

    public BitFields addField(int length, String name) {
        fields.add();
        fields.add(new Tuple<>(totalBits, name));

        return this;
    }

    public OpArgs getArguments(BitInputStream inputStream) {
        return null;
    }

    public static class BitField {
        private BitCondition conditions[];
        public void setConditions(BitCondition conditions[]) {
            this.conditions = conditions;
        }

        public int getValue(BitInputStream inputStream) {
            return 0;
        }
    }

    public static interface BitCondition {
        public boolean get(int index, BitField previous, BitField next, BitFields self);
    }
}
