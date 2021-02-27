package org.wolkenproject.core.script;

public abstract class Script {
    public abstract void fromCompressedFormat(byte data[]);
    public abstract byte[] getCompressed();
}
