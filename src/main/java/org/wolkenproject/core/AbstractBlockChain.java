package org.wolkenproject.core;

import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.messages.BlockList;
import org.wolkenproject.network.messages.Inv;
import org.wolkenproject.network.messages.RequestBlocks;
import org.wolkenproject.network.messages.RequestHeadersBefore;
import org.wolkenproject.utils.Logger;
import org.wolkenproject.utils.Null;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBlockChain implements Runnable {
    // contains rejected blocks.
    private Context         context;
    // a mutex
    private ReentrantLock   mutex;

    public AbstractBlockChain(Context context) {
        this.context    = context;
        this.mutex      = new ReentrantLock();
    }
}
