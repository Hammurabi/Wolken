package org.wolkenproject.core;

import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;

public class Address {
    private byte[] address;

    public Address(byte address[]) {
        this.address = address;
    }

    public byte[] generate(byte prefix, byte publicKeyBytes[]) {
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
