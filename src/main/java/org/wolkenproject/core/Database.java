package org.wolkenproject.core;

import org.iq80.leveldb.DB;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.FileService;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
        database= Iq80DBFactory.factory.open(location.newFile("leveldb").file(), new Options());
        mutex   = new ReentrantLock();
    }

    public void storeBlockHashFromHeight(long height, byte hash[])
    {
        mutex.lock();
        try {
            byte id[]   = Utils.concatenate(BlockLookupFromHeight, Utils.takeApartLong(height));
            database.put(id, hash);
        } finally {
            mutex.unlock();
        }
    }

    public LookupResult<byte[]> findBlockHashFromHeight(long height)
    {
        mutex.lock();
        try {
            byte id[]   = Utils.concatenate(BlockLookupFromHeight, Utils.takeApartLong(height));
            byte data[] = database.get(id);

            return new LookupResult<>(data, data != null);
        } finally {
            mutex.unlock();
        }
    }

    public void storeOutput(byte[] txid, char index, Output output) throws WolkenException {
        mutex.lock();
        try {
            byte id[]   = Utils.concatenate(UnspentTransactionOutput, Utils.concatenate(txid, Utils.takeApartChar(index)));
            byte data[] = database.get(id);

            database.put(id, output.asByteArray());
        } finally {
            mutex.unlock();
        }
    }

    public LookupResult<Output> findOutput(byte[] txid, char index) throws WolkenException {
        mutex.lock();
        try {
            byte id[]   = Utils.concatenate(UnspentTransactionOutput, Utils.concatenate(txid, Utils.takeApartChar(index)));
            byte data[] = database.get(id);

            return new LookupResult<>(new Output(data), data != null);
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

    public BlockHeader findBlockHeaderByHeight(long height) {
        LookupResult<byte[]> blockHash = findBlockHashFromHeight(height);
        if (blockHash.exists())
        {
        }

        return null;
    }

    public Set<byte[]> getNonDuplicateTransactions(Set<byte[]> list) {
        Set<byte[]> result = new HashSet<>();
        for (byte[] txid : list)
        {
            byte id[]   = Utils.concatenate(TransactionFromHash, Utils.concatenate(txid));
            if (database.get(id) == null)
            {
                result.add(txid);
            }
        }

        return result;
    }

    public TransactionI getTransaction(byte[] txid) {
        return null;
    }

    public void setTip(BlockIndex block) {
    }

    public boolean checkBlockExists(byte[] hash) {
        return false;
    }

    public BlockIndex findBlock(byte[] hash) {
        return null;
    }

    public void setBlockIndex(int height, BlockIndex block) {
    }
}
