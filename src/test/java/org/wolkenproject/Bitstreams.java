package org.wolkenproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wolkenproject.utils.BitInputStream;
import org.wolkenproject.utils.BitOutputStream;
import org.wolkenproject.utils.Utils;

import java.io.IOException;

public class Bitstreams {
    @Test
    void bitStreamReadWrite() throws IOException {
        int testBytes[] = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        String bitStrings[] = new String[testBytes.length];

        BitOutputStream bitOutputStream = new BitOutputStream();
        for (int x = 0; x < testBytes.length; x++) {
            StringBuilder str = new StringBuilder();

            for (int i = 0; i < 4; i ++) {
                bitOutputStream.write(Utils.getBit(testBytes[x], i));
                str.append(Utils.getBit(testBytes[x], i));
            }

            bitStrings[x] = str.toString();
        }

        bitOutputStream.flush();

        BitInputStream bitInputStream = new BitInputStream(bitOutputStream.toByteArray());
        byte bitsBuffer[] = new byte[4];
        for (int i = 0; i < testBytes.length; i ++) {
            StringBuilder str = new StringBuilder();
            for (int b = 0; b < bitsBuffer.length; b ++) {
                int read = bitInputStream.read();;

                if (read == -1) {
                    break;
                }

                bitsBuffer[b] = (byte) read;
                str.append(bitsBuffer[b]);
            }

            Assertions.assertEquals(str.toString(), bitStrings[i]);
            Assertions.assertEquals(Utils.makeByte(bitsBuffer), testBytes[i]);
        }
    }
}
