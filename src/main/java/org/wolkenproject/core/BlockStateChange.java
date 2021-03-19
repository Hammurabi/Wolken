package org.wolkenproject.core;

import org.wolkenproject.core.events.AliasRegistrationEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlockStateChange {
    private Queue<byte[]>   transactionIds;
    private Queue<byte[]>   transactionEventIds;
    private List<Event>     transactionEvents;

    public BlockStateChange() {
        this.transactionIds         = new LinkedList<>();
        this.transactionEventIds    = new LinkedList<>();
        this.transactionEvents      = new LinkedList<>();
    }

    public boolean checkAliasExists(long alias) {
        if (Context.getInstance().getDatabase().checkAccountExists(alias)) {
            return true;
        }

        for (Event event : transactionEvents) {
            if (event instanceof AliasRegistrationEvent) {
                if (((AliasRegistrationEvent) event).getAlias() == alias) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkAccountExists(byte address[]) {
    }

    public BlockStateChangeResult getResult() {
        return new BlockStateChangeResult(transactionIds, transactionEventIds, transactionEvents);
    }
}
