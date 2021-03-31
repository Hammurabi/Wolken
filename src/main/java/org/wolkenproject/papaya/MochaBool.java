package org.wolkenproject.papaya;

import org.wolkenproject.papaya.internal.MochaObject;

public class MochaBool extends MochaObject {
    private boolean bool;

    public MochaBool(boolean b) {
        this.bool = b;
    }

    @Override
    public boolean isTrue() {
        return bool;
    }
}
