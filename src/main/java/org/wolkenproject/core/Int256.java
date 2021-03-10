package org.wolkenproject.core;

public class Int256 {
    public int      data[];
    public boolean  signed;

    public Int256(int data[], boolean signed) {
        this.data   = data;
        this.signed = signed;
    }

    public void add(Int256 other) {
        int carry   = 0;
        int sum     = 0;

        for (int i = 0; i < 4; i ++) {
            for (int b = 0; b < 32; b ++) {
            }
        }
    }
}
