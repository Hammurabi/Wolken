package org.wolkenproject.core;

import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.ChainMath;

public abstract class AbstractMiner implements Runnable {
    private Address         miningAddress;

    public AbstractMiner(Address miningAddress) {
        this.miningAddress = miningAddress;
    }

    public abstract void mine(Block block) throws WolkenException;

    @Override
    public void run() {
        while (Context.getInstance().isRunning()) {
            try {
                // get a reference parent block
                BlockIndex parent = Context.getInstance().getBlockChain().getTip();
                // generate a new block
                Block block = new Block();
                // mint coins to our address
                block.addTransaction(Transaction.newMintTransaction("", ChainMath.getReward(parent.getHeight()), miningAddress));

                // add transactions
                addTransactions(block);
                // chain the block
                block.setParent(parent.getHash());
                // generate or reuse bits
                block.setBits(ChainMath.calculateNewTarget(block, parent.getHeight() + 1));

                // build the block and calculate all the remaining elements needed
                block.build(parent.getHeight() + 1);

                // mine the block
                mine(block);

                // create a block index
                BlockIndex index = new BlockIndex(block, parent.getChainWork(), parent.getHeight());

                // submit the block
                Context.getInstance().getBlockChain().suggest(index);
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
