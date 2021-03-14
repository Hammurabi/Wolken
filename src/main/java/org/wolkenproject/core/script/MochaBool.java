package org.wolkenproject.core.script;

import org.wolkenproject.core.script.internal.MochaObject;

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
