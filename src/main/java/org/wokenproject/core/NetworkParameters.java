package org.wokenproject.core;

import org.json.JSONObject;
import org.wokenproject.encoders.Base16;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.utils.ChainMath;

import java.math.BigInteger;

public class NetworkParameters {
    private boolean isTestNet;
    private byte    defaultBits[];

    private BigInteger maximumTarget;

    NetworkParameters(boolean testNet) throws WolkenException {
        this.isTestNet = testNet;

        if (testNet) {
            defaultBits = Base16.decode("1e00ffff");
        }
        else {
            defaultBits = Base16.decode("1d00ffff");
        }

        this.maximumTarget      = ChainMath.targetIntegerFromBits(defaultBits);
    }

    public boolean isTestNet() {
        return isTestNet;
    }

    public int getMaxTransactionList() {
        return 50;
    }

    public int getMaxBlockWeight() {
        return 1_000_000;
    }

    public int getMaxBlockSize() {
        return 200_000;
    }

    public int getAverageBlockTime() {
        return 12;
    }

    public byte[] getDefaultBits() {
        return defaultBits;
    }

    public byte[] getEmptyChainLink() {
        return new byte[20];
    }

    public int getCoinbaseLockTime() {
        return 7200;
    }

    public BigInteger getMaxTarget() {
        return maximumTarget;
    }

    public int getNonceLength() {
        return 24;
    }

    public int getDifficultyAdjustmentThreshold() {
        return 1800;
    }

    public long blocksPerYear() {
        return 2_625_000L;
    }

    public long getHalvingRate() {
        return blocksPerYear() * 4;
    }

    public long getMaxReward() {
        return getOneCoin();
    }

    /**
     *
     * Term: 37 halvings or roughly 148 years.
     *
     */
    public long getMaxResources() {
        return 21_000_000__00_000_000_000L;
    }

    public long getOneCoin() {
        return 1___________00_000_000_000L;
    }

    public byte getGenericAddressPrefix() {
        if (isTestNet) {
            return 43;
        }

        return 53;
    }

    public byte getContractAddressPrefix() {
        if (isTestNet) {
            return 41;
        }

        return 51;
    }

    public int getBufferSize() {
        return 16384;
    }

    public int getMaxBytesReceive() {
        return 4_000_000;
    }

    public int getMaxBytesSend() {
        return getMaxBytesReceive();
    }

    public int getMaxCacheReuse() {
        return 5;
    }

    public long getHandshakeTimeout() {
        return 2_500;
    }

    public int getMaxNetworkErrors() {
        return 25;
    }

    public int getMaxAllowedInboundConnections() {
        return 125;
    }

    public int getMaxAllowedOutboundConnections() {
        return 8;
    }

    public int getPort() {
        return isTestNet ? 5112 : 5110;
    }
}
