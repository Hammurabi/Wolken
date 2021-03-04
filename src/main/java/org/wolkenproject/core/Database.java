package org.wolkenproject.core;

import org.iq80.leveldb.DB;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.FileService;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private DB              database;
    private FileService     location;
    private ReentrantLock   mutex;

    private final static byte[]
    UnspentTransactionOutput= Utils.takeApartShort((short) 1),
    ChainTip                = Utils.takeApartShort((short) 2),
    BlockIndex              = Utils.takeApartShort((short) 3);

    public Database(FileService location) throws IOException {
        database= Iq80DBFactory.factory.open(location.newFile(".db").file(), new Options());
        this.location = location;
        mutex   = new ReentrantLock();
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

    public void setTip(BlockIndex block) {
        mutex.lock();
        try {
            database.put(ChainTip, Utils.concatenate(Utils.concatenate(block.getBlock().getHashCode(), Utils.takeApart(block.getHeight()))));
        } finally {
            mutex.unlock();
        }
    }

    public boolean checkBlockExists(byte[] hash) {
        return location.newFile(".chain").newFile(Base16.encode(hash)).exists();
    }

    public BlockIndex findBlock(byte[] hash) {
        mutex.lock();
        try {
            InputStream inputStream = location.newFile(".chain").newFile(Base16.encode(hash)).openFileInputStream();
            BlockIndex block = Context.getInstance().getSerialFactory().fromStream(BlockIndex.class, inputStream);
            inputStream.close();

            return block;
        } catch (IOException | WolkenException e) {
            e.printStackTrace();
            return null;
        } finally {
            mutex.unlock();
        }
    }

    public void setBlockIndex(int height, BlockIndex block) {
    }

    public BlockIndex findBlock(int height) {
        byte hash[] = get(Utils.concatenate(Database.BlockIndex, Utils.takeApart(height)));

        if (hash == null) {
            return null;
        }
        return findBlock(hash);
    }

    public void deleteBlock(byte[] hash) {
    }

    public byte[] get(byte[] k) {
        mutex.lock();
        try {
            return get(k);
        } finally {
            mutex.unlock();
        }
    }

    public byte[] put(byte[] k, byte[] v) {
        mutex.lock();
        try {
            return get(k);
        } finally {
            mutex.unlock();
        }
    }
}
