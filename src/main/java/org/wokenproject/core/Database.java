package org.wokenproject.core;

import org.iq80.leveldb.DB;
import org.wokenproject.utils.FileService;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import sun.awt.Mutex;

import java.io.IOException;

public class Database {
    private DB      db;
    private Mutex   mutex;

    public Database(FileService location) throws IOException {
        db      = Iq80DBFactory.factory.open(location.file(), new Options());
        mutex   = new Mutex();
    }

    public LookupResult<Output> findOutput(byte[] txid, int index)
    {
        return new LookupResult<>(null, false);
    }
}
