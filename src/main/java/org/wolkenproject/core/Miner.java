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
            try {
                // get a reference parent block
                BlockIndex parent = Context.getInstance().getBlockChain().getTip();
                // generate a new block
                Block block = new Block();
                // mint coins to our address
                block.addTransaction(Transaction.newCoinbase("", ChainMath.getReward(parent.getHeight()), miningAddress));
                // add transactions
                addTransactions(block);

                // create a block-index
                block.setParent(parent.getHash());
                block.setBits(ChainMath.calculateNewTarget(block, parent.getHeight() + 1));

                // build the block and calculate all the remaining elements needed
                block.build();
            } catch (WolkenException e) {
                e.printStackTrace();
            }
        }
    }

    protected void addTransactions(Block block) {
        while (block.calculateSize() < Context.getInstance().getNetworkParameters().getMaxBlockSize()) {
            block.addTransaction(Context.getInstance().getTransactionPool().pollTransaction());
        }

        if (block.calculateSize() > Context.getInstance().getNetworkParameters().getMaxBlockSize()) {
            block.removeLastTransaction();
        }
    }
}
