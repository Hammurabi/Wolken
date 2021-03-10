package org.wolkenproject;

import org.junit.jupiter.api.Test;
import org.wolkenproject.core.Int256;

public class Int256Tests {
    @Test
    void testInt256() {
        Int256 first    = new Int256(1);
        Int256 second   = new Int256(2);
        long value      = first.add(second).asLong();
    }
}
