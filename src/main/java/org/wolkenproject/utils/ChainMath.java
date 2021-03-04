package org.wolkenproject.utils;

import org.wolkenproject.core.Block;
import org.wolkenproject.core.BlockHeader;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.WolkenException;

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

    public static int calculateNewTarget(BlockIndex block) throws WolkenException {
        int currentBlockHeight = block.getHeight();

        if (shouldRecalcNextWork(currentBlockHeight)) {
            BlockIndex first = null;

            int previousBlockHeight = currentBlockHeight - Context.getInstance().getNetworkParameters().getDifficultyAdjustmentThreshold();

            if (previousBlockHeight >= 0) {
                first = Context.getInstance().getDatabase().findBlock(previousBlockHeight);
            }

            return generateTargetBits(block, first);
        }

        return block.getBlock().getBits();
    }

    private static int generateTargetBits(BlockIndex latest, BlockIndex earliest) throws WolkenException {
        //calculate the target time for 1800 blocks.
        long timePerDiffChange  = Context.getInstance().getNetworkParameters().getAverageBlockTime() * Context.getInstance().getNetworkParameters().getDifficultyAdjustmentThreshold();
        long averageNetworkTime = latest.getBlock().getTimestamp() - earliest.getBlock().getTimestamp();

        return generateTargetBits(averageNetworkTime, timePerDiffChange, latest.getBlock().getBits());
    }


    private static int generateTargetBits(long actualTimespan, long timeRequired, int prevTarget) throws WolkenException {
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

    protected static final int getCompact(BigInteger integer)
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

        return Utils.makeInt(bits);
    }
}
