package org.wokenproject.utils;

import org.wokenproject.core.Block;
import org.wokenproject.core.BlockHeader;
import org.wokenproject.core.Context;
import org.wokenproject.encoders.Base16;
import org.wokenproject.exceptions.WolkenException;

import java.math.BigInteger;

public class ChainMath {
    public static BigInteger x256 = new BigInteger("2").pow(256);

    public static boolean validSolution(byte solution[], byte targetBits[]) throws WolkenException {
        return new BigInteger(1, solution).compareTo(targetIntegerFromBits(targetBits)) < 0;
    }

    public static boolean lessThan(byte a[], byte b[]) {
        if (a.length<b.length) {
            int diff = b.length-a.length;
            for (int i = 0; i < diff; i ++) {
                if (Byte.toUnsignedInt(b[i]) != 0) {
                    return true;
                }
            }

            for (int i = 0; i < a.length; i ++) {
                if (Byte.toUnsignedInt(a[i]) >= Byte.toUnsignedInt(b[i + diff])) {
                    return false;
                } else {
                    return true;
                }
            }
        } else if (b.length<a.length) {
            int diff = a.length-b.length;
            for (int i = 0; i < diff; i ++) {
                if (Byte.toUnsignedInt(a[i]) != 0) {
                    return false;
                }
            }

            for (int i = 0; i < b.length; i ++) {
                if (Byte.toUnsignedInt(a[i + diff]) >= Byte.toUnsignedInt(b[i])) {
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < a.length; i ++) {
                if (Byte.toUnsignedInt(a[i]) >= Byte.toUnsignedInt(b[i])) {
                    return false;
                } else return true;
            }
        }

        return true;
    }

    public static byte[] targetFromBits(byte bits[]) throws WolkenException {
        int length      = Byte.toUnsignedInt(bits[0]);
        if (length > 32)
            throw new WolkenException("invalid target bits '" + Base16.encode(bits) + "'.");

        byte target[]   = Utils.fillArray(new byte[length], (byte) 255);

        target[0]       = bits[1];
        target[1]       = bits[2];
        target[2]       = bits[3];

        return target;
    }

    public static byte[] targetFromBits256(byte bits[]) throws WolkenException {
        byte target[] = targetFromBits(bits);

        if (target.length < 32) {
            return Utils.concatenate(new byte[32 - target.length], target);
        }

        return target;
    }

    public static BigInteger targetIntegerFromBits(int bits) throws WolkenException {
        return targetIntegerFromBits(Utils.takeApart(bits));
    }

    public static BigInteger targetIntegerFromBits(byte bits[]) throws WolkenException {
        int length      = Byte.toUnsignedInt(bits[0]);
        if (length > 32)
            throw new WolkenException("invalid target bits '" + Base16.encode(bits) + "'.");

        byte target[]   = Utils.fillArray(new byte[length], (byte) 255);

        target[0]       = bits[1];
        target[1]       = bits[2];
        target[2]       = bits[3];

        return new BigInteger(1, target);
    }

    public static long getReward(long currentHeight) {
        long height = currentHeight;
        if (height == 0) {
            //this number could be used to account for the loss of 0.001575 Sats
            //            return 100157500000L;
            height = 1;
        }

        long numberOfHalvings = height / Context.getInstance().getNetworkParameters().getHalvingRate();

        if (numberOfHalvings >= 37) {
            return 0;
        }

        BigInteger D = new BigInteger("2").pow((int) numberOfHalvings);
        long asLong = D.longValue();

        if (asLong == 0)
            return 0L;

        return Context.getInstance().getNetworkParameters().getMaxReward() / asLong;
    }

    public static BigInteger getTotalWork(byte[] bits) throws WolkenException {
        return x256.divide(new BigInteger(1, targetFromBits(bits)));
    }

    public static boolean shouldRecalcNextWork(long height) {
        return (height + 1) % Context.getInstance().getNetworkParameters().getDifficultyAdjustmentThreshold() == 0;
    }

    public static byte[] calculateNewTarget(Block parent) throws WolkenException {
        int currentBlockHeight = parent.getHeight();
        if (shouldRecalcNextWork(currentBlockHeight)) {
            BlockHeader header = null;

            int previousBlockHeight = currentBlockHeight - Context.getInstance().getNetworkParameters().getDifficultyAdjustmentThreshold();

            if (previousBlockHeight >= 0) {
                header = Context.getInstance().getDatabase().findBlockHeaderByHeight(previousBlockHeight);
            }

            return generateTargetBits(parent, header);
        }

        return parent.getBits();
    }

    private static byte[] generateTargetBits(Block parent, BlockHeader first) throws WolkenException {
        //calculate the target time for 1800 blocks.
        long timePerDiffChange  = Context.getInstance().getNetworkParameters().getAverageBlockTime() * Context.getInstance().getNetworkParameters().getDifficultyAdjustmentThreshold();
        long averageNetworkTime = parent.getTimestamp() - first.getTimestamp();

        return generateTargetBits(averageNetworkTime, timePerDiffChange, parent.getBits());
    }


    private static byte[] generateTargetBits(long actualTimespan, long timeRequired, byte prevTarget[]) throws WolkenException {
        BigInteger target = targetIntegerFromBits(prevTarget);

        if (actualTimespan < (timeRequired / 4)) {
            actualTimespan = (timeRequired / 4);
        }

        if (actualTimespan > (timeRequired * 4)) {
            actualTimespan = (timeRequired * 4);
        }

        target = target.multiply(new BigInteger(Long.toString(actualTimespan)))
                .divide(new BigInteger(Long.toString(timeRequired)));
        if (target.compareTo(Context.getInstance().getNetworkParameters().getMaxTarget()) > 0) {
            target = Context.getInstance().getNetworkParameters().getMaxTarget();
        }

        return getCompact(target);
    }

    protected static final byte[] getCompact(BigInteger integer)
    {
        byte bytes[]            = integer.toByteArray();
        byte bits[]             = new byte[4];

        bits[0]                 = (byte) bytes.length;
        if(bytes.length > 0)
            bits[1]             = bytes[0];
        if(bytes.length > 1)
            bits[2]             = bytes[1];
        if(bytes.length > 2)
            bits[3]             = bytes[2];

        return bits;
    }
}
