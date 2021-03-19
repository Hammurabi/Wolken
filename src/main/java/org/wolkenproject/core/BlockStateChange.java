package org.wolkenproject.core;

import org.wolkenproject.core.events.RegisterAliasEvent;
import org.wolkenproject.core.events.NewAccountEvent;

import java.util.Arrays;
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
            if (event instanceof RegisterAliasEvent) {
                if (((RegisterAliasEvent) event).getAlias() == alias) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkAccountExists(byte address[]) {
        if (Context.getInstance().getDatabase().checkAccountExists(address)) {
            return true;
        }

        for (Event event : transactionEvents) {
            if (event instanceof NewAccountEvent) {
                if (Arrays.equals(((NewAccountEvent) event).getAddress(), address)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void addEvent(Event event) {
        transactionEvents.add(event);
        transactionEventIds.add(event.eventId());
    }

    public void addEvents(List<Event> events) {
        transactionEvents.addAll(events);
        for (Event event : events) {
            transactionEventIds.add(event.eventId());
        }
    }

    public BlockStateChangeResult getResult() {
        return new BlockStateChangeResult(transactionIds, transactionEventIds, transactionEvents);
    }

    public void addTransaction(byte[] transactionID) {
        transactionIds.add(transactionID);
    }
}
