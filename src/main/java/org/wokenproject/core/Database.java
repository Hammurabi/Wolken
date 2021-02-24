package org.wokenproject.core;

import org.iq80.leveldb.DB;
import org.wokenproject.utils.FileService;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.wokenproject.utils.Utils;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private DB              database;
    private ReentrantLock   mutex;

    private final static byte[]
    UnspentTransactionOutput= Utils.takeApartShort((short) 1),
    TransactionFromHash     = Utils.takeApartShort((short) 2),
    ChainTipHash            = Utils.takeApartShort((short) 3),
    ChainWork               = Utils.takeApartShort((short) 4),
    BlockLookupFromHeight   = Utils.takeApartShort((short) 5),
    BlockLookup             = Utils.takeApartShort((short) 6)
    ;

    public Database(FileService location) throws IOException {
        database= Iq80DBFactory.factory.open(location.file(), new Options());
        mutex   = new ReentrantLock();
    }

    public LookupResult<Output> findOutput(byte[] txid, char index)
    {
        mutex.lock();
        try {
            byte id[]   = Utils.concatenate(UnspentTransactionOutput, Utils.concatenate(txid, Utils.takeApartChar(index)));
            byte data[] = database.get(id);
            if (data == null)
            {
                return new LookupResult<>(null, false);
            }

            return new LookupResult<>(new Output(data), true);
        } finally {
            mutex.unlock();
        }
    }

    public boolean getOutputExists(byte[] txid, char index)
    {
        mutex.lock();
        try {
            byte id[]   = Utils.concatenate(UnspentTransactionOutput, Utils.concatenate(txid, Utils.takeApartChar(index)));
            byte data[] = database.get(id);

            return data != null;
        } finally {
            mutex.unlock();
        }
    }
}
