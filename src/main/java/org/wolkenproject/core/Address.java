package org.wolkenproject.core;

public class Address {
    public static boolean isValidAddress(byte[] address) {
        if (address.length != 25) {
            return false;
        }

        return true;
    }
}
