package org.wolkenproject.core.script;

import org.wolkenproject.core.Address;

public abstract class Script {
    public static byte[] newP2PKH(Address address) {
        return new byte[0];
    }

    public abstract void fromCompressedFormat(byte data[]);
    public abstract byte[] getCompressed();
}
