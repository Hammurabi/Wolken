package org.wolkenproject.core;

import java.util.List;

public class PrunedBlock {
    private final BlockHeader   blockHeader;
    private final List<byte[]>  transactions;
    private final List<byte[]>  events;
}
