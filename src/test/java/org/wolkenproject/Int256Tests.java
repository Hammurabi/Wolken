package org.wolkenproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wolkenproject.core.Int256;

public class Int256Tests {
    @Test
    void testInt256() {
        {
            for (int i = 0; i < 20; i ++) {
                long f          = (i * 1000) * 1000;
                long s          = (i * 1000) * 1000 + 1;

                Int256 first    = new Int256(f);
                Int256 second   = new Int256(s);
                long value      = first.add(second).asLong();
                Assertions.assertEquals(f + s, value);
            }
        }
    }
}
