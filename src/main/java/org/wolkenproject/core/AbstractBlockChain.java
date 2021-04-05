package org.wolkenproject.core;

import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBlockChain implements Runnable {
    private final Context       context;
    private final ReentrantLock mutex;

    public AbstractBlockChain(Context context) {
        this.context    = context;
        this.mutex      = new ReentrantLock();
    }

    public Context getContext() {
        return context;
    }

    public ReentrantLock getMutex() {
        return mutex;
    }
}
