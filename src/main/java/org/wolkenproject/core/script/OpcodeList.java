package org.wolkenproject.core.script;

import org.wolkenproject.serialization.SerializableI;

public class OpcodeList extends SerializableI {
    // this class should be used to store opcodes
    // it will compact them in the most optimal
    // way for storage/network transfers to help
    // reduce the cost of transactions.

    // contains bits from arguments
    private long argumentBits;
}
