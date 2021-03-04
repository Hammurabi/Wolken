package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;

import java.util.Comparator;

public class BlockIndexComparator implements Comparator<BlockIndex> {
    @Override
    public int compare(BlockIndex a, BlockIndex b) {
        try {
            int compare = a.getChainWork().compareTo(b.getChainWork());

            if (compare > 0) {
                return -1;
            }

            if (compare < 0) {
                return 1;
            }

            if (a.getSequenceId() < b.getSequenceId()) {
                return -1;
            }

            if (a.getSequenceId() > b.getSequenceId()) {
                return 1;
            }

            if (a.getBlock().getTransactionCount() > b.getBlock().getTransactionCount()) {
                return 1;
            }

            if (a.getBlock().getTransactionCount() < b.getBlock().getTransactionCount()) {
                return -1;
            }

            return -1;
        } catch (WolkenException e) {
            return 0;
        }
    }
}
