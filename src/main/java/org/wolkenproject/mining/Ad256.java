package org.wolkenproject.mining;

import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Utils;

import java.util.Arrays;

import static java.lang.System.arraycopy;

public class Ad256 {
    private static final int FNV_PRIME = 0x01000193;

    // returns a 1024 byte seed from
    public static byte[] calculateSeed(int block) {
        return null;
    }

    public static byte[] f_3(byte[] bytes) {
        return new byte[] {
                bytes[1],
                bytes[3],
                bytes[0],
                bytes[2]
        };
    }

    private static int fnv(int v1, int v2) {
        return (v1 * FNV_PRIME) ^ v2;
    }

    public static final int[] calcDatasetItem(Params params, final int[] cache, final int i) {
        final int r = params.getHASH_BYTES() / params.getWORD_BYTES();
        final int n = cache.length / r;
        int[] mix = Arrays.copyOfRange(cache, i % n * r, (i % n + 1) * r);

        mix[0] = i ^ mix[0];
        mix = sha512(mix, false);
        final int dsParents = (int) params.getDATASET_PARENTS();
        final int mixLen = mix.length;
        for (int j = 0; j < dsParents; j++) {
            int cacheIdx = fnv(i ^ j, mix[j % r]);
            cacheIdx = remainderUnsigned(cacheIdx, n);
            int off = cacheIdx * r;
            for (int k = 0; k < mixLen; k++) {
                mix[k] = fnv(mix[k], cache[off + k]);
            }
        }
        return sha512(mix, false);
    }

    public int[] calcDataset(long fullSize, int[] cache) {
        int hashesCount = (int) (fullSize / params.getHASH_BYTES());
        int[] ret = new int[hashesCount * (params.getHASH_BYTES() / 4)];
        for (int i = 0; i < hashesCount; i++) {
            int[] item = calcDatasetItem(cache, i);
            arraycopy(item, 0, ret, i * (params.getHASH_BYTES() / 4), item.length);
        }
        return ret;
    }

    public static byte[] hash(Params params, byte header[], int nonceInteger) {
        byte nonce[]= Utils.takeApart(nonceInteger);

        // hash the header to generate a 64-byte 16 int seed.
        int seed[] = Utils.makeInts(HashUtil.sha512(header));
        // mix bytes.
        int[] mix = new int[params.getMIX_BYTES() / 4];

        int hashWords   = params.getHASH_BYTES() / 4;
        int w           = params.getMIX_BYTES() / params.getWORD_BYTES();
        int mixhashes   = params.getMIX_BYTES() / params.getHASH_BYTES();

        for (int i = 0; i < mixhashes; i++) {
            arraycopy(seed, 0, mix, i * seed.length, seed.length);
        }

        int numFullPages = (int) (fullSize / params.getMIX_BYTES());
    }
}
