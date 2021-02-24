package org.wokenproject.core;

import org.iq80.leveldb.DB;
import org.wokenproject.utils.FileService;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private DB              db;
    private ReentrantLock   mutex;

    private final static byte[]
    ChainWork               = {'c', 'w'},
    BlockLookupFromHeight   = {'b', 'h'},
    BlockLookup             = {'b'}
    ;

    public Database(FileService location) throws IOException {
        db      = Iq80DBFactory.factory.open(location.file(), new Options());
        mutex   = new ReentrantLock();
    }

    public LookupResult<Output> findOutput(byte[] txid, int index)
    {
        mutex.lock();
        try {

        } finally {
            mutex.unlock();
        }

        return new LookupResult<>(null, false);
    }
}
