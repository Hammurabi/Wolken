package org.wolkenproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VarInt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VarIntTests {
    @Test
    public void testReadWriteLossy32() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int ints[] = new int[] {
                0, 1, 2, 5, 10, 20, 22, 30, 32, 40, 60, 120, 12000, 24000, 124000, 1000000, 1000000000
        };

        byte empty[] = new byte[12];

        for (int i : ints) {
            VarInt.writeCompactUInt32(i, false, outputStream);
            outputStream.write(empty);
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        for (int i : ints) {
            Assertions.assertEquals(i, VarInt.readCompactUInt32(false, inputStream));
            Assertions.assertEquals(inputStream.read(empty), empty.length);
            Assertions.assertTrue(Utils.isEmpty(empty), "array should consist of zeros.");
        }
    }

    @Test
    public void testReadWriteLossless32() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int ints[] = new int[] {
                0, 1, 2, 5, 10, 20, 22, 30, 32, 40, 60, 120, 12000, 24000, 124000, 1000000, 1000000000
        };

        byte empty[] = new byte[12];

        for (int i : ints) {
            VarInt.writeCompactUInt32(i, true, outputStream);
            outputStream.write(empty);
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        for (int i : ints) {
            Assertions.assertEquals(i, VarInt.readCompactUInt32(true, inputStream));
            Assertions.assertEquals(inputStream.read(empty), empty.length);
            Assertions.assertTrue(Utils.isEmpty(empty), "array should consist of zeros.");
        }
    }
}
