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
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import static org.wolkenproject.utils.Utils.concatenate;

public class Database {
    private DB              database;
    private FileService     location;
    private ReentrantLock   mutex;

    private final static byte[]
    Account                 = Utils.takeApartShort((short) 1),
    ChainTip                = Utils.takeApartShort((short) 2),
    BlockHeader             = Utils.takeApartShort((short) 3),
    BlockIndex              = Utils.takeApartShort((short) 4),
    Transaction             = Utils.takeApartShort((short) 5),
    RejectedBlock           = Utils.takeApartShort((short) 6);

    public Database(FileService location) throws IOException {
        database= Iq80DBFactory.factory.open(location.newFile(".db").file(), new Options());
        this.location = location;
        mutex   = new ReentrantLock();
    }

    public void setTip(BlockIndex block) {
        put(ChainTip, concatenate(concatenate(block.getHash(), Utils.takeApart(block.getHeight()))));
    }

    public boolean checkBlockExists(byte[] hash) {
        return location.newFile(".chain").newFile(Base16.encode(hash)).exists();
    }

    public boolean checkBlockExists(int height) {
        byte hash[] = get(concatenate(Database.BlockIndex, Utils.takeApart(height)));

        return hash != null;
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
        put(concatenate(Database.BlockIndex, Utils.takeApart(height)), block.getHash());
        mutex.lock();
        try {
            OutputStream outputStream = location.newFile(".chain").newFile(Base16.encode(block.getHash())).openFileOutputStream();
            block.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException | WolkenException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }

    public BlockIndex findBlock(int height) {
        byte hash[] = get(concatenate(Database.BlockIndex, Utils.takeApart(height)));

        if (hash == null) {
            return null;
        }

        return findBlock(hash);
    }

    public void deleteBlock(int height) {
        byte key[]  = concatenate(Database.BlockIndex, Utils.takeApart(height));
        byte hash[] = get(key);

        if (checkBlockExists(hash)) {
            location.newFile(".chain").newFile(Base16.encode(hash)).delete();
        }

        if (hash != null) {
            remove(key);
        }

    }

    public byte[] get(byte[] k) {
        mutex.lock();
        try {
            return database.get(k);
        } finally {
            mutex.unlock();
        }
    }

    public void put(byte[] k, byte[] v) {
        mutex.lock();
        try {
            database.put(k, v);
        } finally {
            mutex.unlock();
        }
    }

    public void remove(byte[] k) {
        mutex.lock();
        try {
            database.delete(k);
        } finally {
            mutex.unlock();
        }
    }

    public BlockIndex findTip() {
        byte tip[] = get(ChainTip);

        if (tip != null) {
            return findBlock(Utils.trim(tip, 0, 32));
        }

        return null;
    }

    public byte[] findBlockHash(int height) {
        return get(concatenate(Database.BlockIndex, Utils.takeApart(height)));
    }

    public BlockHeader findBlockHeader(byte[] hash) {
        mutex.lock();
        try {
            InputStream inputStream = location.newFile(".chain").newFile(Base16.encode(hash)).openFileInputStream();
            BlockHeader header  = Context.getInstance().getSerialFactory().fromStream(BlockHeader.class, inputStream);
            inputStream.close();

            return header;
        } catch (IOException | WolkenException e) {
            e.printStackTrace();
            return null;
        } finally {
            mutex.unlock();
        }
    }

    public BlockHeader findBlockHeader(int height) {
        byte hash[] = findBlockHash(height);
        if (hash != null) {
            return findBlockHeader(hash);
        }

        return null;
    }

    public Account getAccount(long alias) {
        return null;
    }

    public byte[] getAccountHolder(long alias) {
        return null;
    }

    public Account getAccount(byte address[]) {
        return null;
    }

    public Address getAddressFromAlias(long alias) {
        return null;
    }

    public void storeContract(Address contractAddress, byte contract[]) throws WolkenException {
        mutex.lock();
        try {
            OutputStream outputStream = location.newFile(".contracts").newFile(Base16.encode(contractAddress.getRaw())).openFileOutputStream();
            outputStream.write(contract);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new WolkenException(e);
        } finally {
            mutex.unlock();
        }
    }

    public boolean checkTransactionExists(byte[] txid) {
        return get(concatenate(Transaction, txid)) != null;
    }

    public boolean checkAccountExists(long alias) {
        return false;
    }

    public boolean checkAccountExists(byte[] address) {
        return false;
    }

    public void updateAccount(byte[] accountHolder, Account account) {
    }

    public void newAccount(byte[] address) {
    }

    public void registerAlias(byte[] address, long alias) {
    }

    public void rmvAccount(byte[] address) {
    }

    public void markRejected(byte[] hash) {
        put(Utils.concatenate(RejectedBlock, hash), new byte[] { 1 });
    }
}
