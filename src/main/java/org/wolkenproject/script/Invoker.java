package org.wolkenproject.script;

import org.wolkenproject.core.Address;
import org.wolkenproject.core.BlockStateChange;
import org.wolkenproject.core.transactions.Transaction;

public class Invoker {
    private final BlockStateChange  blockStateChange;
    private final Transaction       transaction;
    private final Address           invoker;

    public Invoker(BlockStateChange blockStateChange, Transaction transaction, Address invoker) {
        this.blockStateChange = blockStateChange;
        this.transaction = transaction;
        this.invoker = invoker;
    }

    public BlockStateChange getBlockStateChange() {
        return blockStateChange;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Address getInvoker() {
        return invoker;
    }
}
