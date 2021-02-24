package org.wokenproject.encoders;

import java.util.Arrays;

public class Base16 {
    private final static char[] HEX = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Convert bytes to a base16 string.
     */
    public static String encode(byte[] byteArray)
    {
        StringBuffer hexBuffer = new StringBuffer(byteArray.length * 2);
        for (int i = 0; i < byteArray.length; i++)
            for (int j = 1; j >= 0; j--)
                hexBuffer.append(HEX[(byteArray[i] >> (j * 4)) & 0xF]);
            
        return hexBuffer.toString();
    }
}
