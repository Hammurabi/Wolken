package org.wolkenproject.core.mocha;

import org.wolkenproject.core.mocha.internal.MochaObject;

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
