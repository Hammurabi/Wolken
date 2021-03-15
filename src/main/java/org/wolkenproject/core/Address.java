package org.wolkenproject.core;

import org.wolkenproject.crypto.Key;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;

public class Address {
    public static final int RawLength = 20;
    private byte[] address;

    private Address(byte address[]) {
        this.address = address;
    }

    // return an address object from a key
    public static Address fromKey(Key key) {
        if (key == null) {
            return null;
        }

        // get the encoded key (with prefix byte)
        byte encodedKey[]   = key.getEncoded();

        // return a hash160 of the key
        return new Address(HashUtil.hash160(encodedKey));
    }

    // return an address object from a 'raw' non-encoded 20 byte address
    public static Address fromRaw(byte[] rawBytes) {
        return new Address(rawBytes);
    }

    public byte[] fromKey(byte prefix, byte publicKeyBytes[]) {
        byte prefixed[] = Utils.concatenate(new byte[] { prefix }, HashUtil.hash160(publicKeyBytes) );
        return Utils.concatenate(prefixed, Utils.trim(HashUtil.sha256d(prefixed), 0, 4));
    }

    public static boolean isValidAddress(byte[] address) {
        if (address.length != 25) {
            return false;
        }

        byte prefixed[] = Utils.trim(address, 0, 21);

        if (prefixed[0] != Context.getInstance().getNetworkParameters().getGenericMainnetAddressPrefix() && prefixed[0] != Context.getInstance().getNetworkParameters().getGenericTestnetAddressPrefix()) {
            return false;
        }

        return Utils.equals(Utils.trim(HashUtil.sha256d(prefixed), 0, 4), Utils.trim(address, 21, 4));
    }
}
