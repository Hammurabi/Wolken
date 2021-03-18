package org.wolkenproject.core;

import java.util.List;

public class BlockStateChangeResult {
    private final List<byte[]> transactionIds;
    private final List<byte[]> transactionEventIds;
    private final List<Event>  transactionEvents;

    public BlockStateChangeResult() {
    }
}
