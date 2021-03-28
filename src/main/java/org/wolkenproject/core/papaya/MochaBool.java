package org.wolkenproject.core.papaya;

import org.wolkenproject.core.papaya.internal.MochaObject;

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
