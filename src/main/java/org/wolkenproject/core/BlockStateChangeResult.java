package org.wolkenproject.core;

import java.util.List;

public class BlockStateChangeResult {
    private final List<byte[]> transactionIds;
    private final List<byte[]> transactionEventIds;
    private final List<Event>  transactionEvents;

    public BlockStateChangeResult(List<byte[]> transactionIds, List<byte[]> transactionEventIds, List<Event> transactionEvents) {
        this.transactionIds         = transactionIds;
        this.transactionEventIds    = transactionEventIds;
        this.transactionEvents      = transactionEvents;
    }
}
