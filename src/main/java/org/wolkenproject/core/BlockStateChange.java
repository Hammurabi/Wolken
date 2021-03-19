package org.wolkenproject.core;

import java.util.List;
import java.util.Queue;

public class BlockStateChange {
    private Queue<byte[]>   transactionIds;
    private Queue<byte[]>   transactionEventIds;
    private List<Event>     transactionEvents;

    public BlockStateChangeResult getResult() {
        return new BlockStateChangeResult(txids, txeids, events);
    }
}
