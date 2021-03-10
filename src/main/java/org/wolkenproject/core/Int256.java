package org.wolkenproject.core;

public class Int256 {
    public int      data[];
    public boolean  signed;

    public Int256(int data[], boolean signed) {
        this.data   = data;
        this.signed = signed;
    }
}
