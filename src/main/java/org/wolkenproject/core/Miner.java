package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.ChainMath;

public abstract class Miner implements Runnable {
    private Address miningAddress;

    public Miner(Address miningAddress) {
        this.miningAddress = miningAddress;
    }

    public abstract void mine();

    @Override
    public void run() {
        while (Context.getInstance().isRunning()) {
            // generate a block
            try {
                Block block = new Block();
                // mint coins to our address
                block.addTransaction(Transaction.newCoinbase("", ChainMath.getReward(blockIndex.getHeight()), miningAddress));
                // add transactions
                addTransactions(block);

                // create a block-index
                BlockIndex parent = Context.getInstance().getBlockChain().getTip();
                block.setParent(parent.getHash());
                block.setBits(ChainMath.calculateNewTarget(block, parent.getHeight() + 1));

                // build the block and calculate all the remaining elements needed
                block.build();
            } catch (WolkenException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void addTransactions(Block block);
}
