package org.wolkenproject.core;

import org.wolkenproject.serialization.SerializableI;

public abstract class SuggestedBlock extends SerializableI {
    public abstract BlockHeader getBlockHeader();
    public abstract BlockIndex getBlock();
}
