package org.wolkenproject.core.script;

public class BitFields {
    private int totalBits;

    public int getTotalBits() {
        return totalBits;
    }

    public int getTotalBytes() {
        return (int) Math.ceil(totalBits / 8.0);
    }
}
