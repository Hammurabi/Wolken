package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.ChainMath;

public class Miner extends AbstractMiner {
    public Miner(Address miningAddress) {
        super(miningAddress);
    }

    @Override
    public void mine(Block block) throws WolkenException {
        BlockHeader header  = block;
        byte hash[]         = header.getHashCode();

        while (!ChainMath.validSolution(hash, header.getBits())) {
            header.setNonce(block.getNonce() + 1);
            hash            = header.getHashCode();
        }
    }
}
