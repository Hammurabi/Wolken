package org.wolkenproject.core.script;

import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.Handler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        public byte[] getValue(BitInputStream inputStream) throws IOException {
            Handler<byte[]> array = new Handler<>(inputStream.getBitsAsByteArray(length));

            if (conditions != null) {
                for (BitCondition condition : conditions) {
                    if (condition.read(inputStream, array)) {
                        return array.get();
                    }
                }
            }

            return array.get();
        }
    }

    public static interface BitCondition {
        public default boolean read(BitInputStream inputStream, Handler<byte[]> value) throws IOException {
            AtomicInteger out = new AtomicInteger(0);

            if (get(value, out)) {
                int length = out.get();
                value.set(inputStream.getBitsAsByteArray(length));

                return true;
            }

            return false;
        }

        public default boolean write(BitInputStream inputStream, Handler<byte[]> value) throws IOException {
            AtomicInteger out = new AtomicInteger(0);

            if (get(value, out)) {
                int length = out.get();
                value.set(inputStream.getBitsAsByteArray(length));

                return true;
            }

            return false;
        }

        public boolean get(Handler<byte[]> value, AtomicInteger out) throws IOException;
    }
}
