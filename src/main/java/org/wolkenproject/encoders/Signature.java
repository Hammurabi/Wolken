package org.wolkenproject.encoders;

public class Signature {
    private final byte v[];
    private final byte r[];
    private final byte s[];

    public Signature(byte[] v, byte[] r, byte[] s) {
        this.v = v;
        this.r = r;
        this.s = s;
    }

    public byte[] getV() {
        return v;
    }

    public byte[] getR() {
        return r;
    }

    public byte[] getS() {
        return s;
    }
}
