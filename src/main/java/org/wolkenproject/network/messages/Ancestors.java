package org.wolkenproject.network.messages;

import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;

import java.util.LinkedHashSet;
import java.util.Set;

public class Ancestors {
    private Set<byte[]> hashes;

    public Ancestors() {
        hashes = new LinkedHashSet<>();
    }

    void fill(BlockIndex tip) {
        int latest      = tip.getHeight() - 1;
        int earliest    = Math.max(0, latest - 16384);
        int total       = latest - earliest;

        int jump        = Math.max(1, total / 128);

        int current     = latest;
        while (current > earliest) {
            byte hash[] = Context.getInstance().getDatabase().findBlockHash(current);
            if (hash != null) {
                hashes.add(hash);
            }

            current -= jump;
        }
    }
}
