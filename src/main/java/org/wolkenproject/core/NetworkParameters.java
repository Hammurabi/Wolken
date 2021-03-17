package org.wolkenproject.core;

import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.VersionInformation;
import org.wolkenproject.utils.ChainMath;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;

public class NetworkParameters {
    private boolean isTestNet;
    private int     defaultBits;

    private BigInteger maximumTarget;

    NetworkParameters(boolean testNet) throws WolkenException {
        this.isTestNet = testNet;

        if (testNet) {
            defaultBits = Utils.makeInt(Base16.decode("1e00ffff"));
        }
        else {
            defaultBits = Utils.makeInt(Base16.decode("1d00ffff"));
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
        return 250_000;
    }

    public int getAverageBlockTime() {
        return 12;
    }

    public int getDefaultBits() {
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
    public long getMaxCoin() {
        return 21_000_000__00_000_000_000L;
    }

    public long getOneCoin() {
        return 1___________00_000_000_000L;
    }

    public long getAliasRegistrationCost() {
        return 5_000L;
    }

    public byte getGenericMainnetAddressPrefix() {
        return 53;
    }

    public byte getGenericTestnetAddressPrefix() {
        return 43;
    }

    public byte getGenericAddressPrefix() {
        if (isTestNet) {
            getGenericTestnetAddressPrefix();
        }

        return getGenericMainnetAddressPrefix();
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

    public int getMaxMessageContentSize() {
        return 8_000_000;
    }

    public int getMaxCacheReuse() {
        return 5;
    }

    public long getHandshakeTimeout() {
        return 2_500;
    }

    public long getMessageTimeout() {
        return 500;
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

    public double getMessageSpamThreshold() {
        return Math.PI;
    }

    public int getMaxCacheSize() {
        return 18_796_99;
    }

    public int getVersion() {
        return 1;
    }

    public boolean isVersionCompatible(int a, int b) {
        return a == b;
    }

    public long getServices() {
        return VersionInformation.Flags.AllServices;
    }

    public Address getFoundingAddresses() {
        return Address.fromRaw(new byte[20]);
    }
}
