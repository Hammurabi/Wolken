package org.wolkenproject.core;

import org.iq80.leveldb.DB;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.FileService;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.wallet.Wallet;

import java.io.ByteArrayInputStream;
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
    AccountPrefix           = Utils.takeApartShort((short) 1),
    AliasPrefix             = Utils.takeApartShort((short) 2),
    ChainTipPrefix          = Utils.takeApartShort((short) 3),
    BlockHeaderPrefix       = Utils.takeApartShort((short) 4),
    BlockContentPrefix      = Utils.takeApartShort((short) 5),
    BlockIndexPrefix        = Utils.takeApartShort((short) 6),
    TransactionPrefix       = Utils.takeApartShort((short) 7),
    RejectedBlockPrefix     = Utils.takeApartShort((short) 8),
    WalletPrefix            = Utils.takeApartShort((short) 9);

    public Database(FileService location) throws IOException {
        location.newFile(".chain").makeDirectories();
        database= Iq80DBFactory.factory.open(location.newFile(".db").file(), new Options());
        this.location = location;
        mutex   = new ReentrantLock();
    }

    public void setTip(BlockIndex block) {
        put(ChainTipPrefix, concatenate(concatenate(block.getHash(), Utils.takeApart(block.getHeight()))));
    }

    public boolean checkBlockExists(byte[] hash) {
        return location.newFile(".chain").newFile(Base16.encode(hash)).exists();
    }

    public boolean checkBlockExists(int height) {
        byte hash[] = get(concatenate(BlockIndexPrefix, Utils.takeApart(height)));

        return hash != null;
    }

    public void storeHeader(byte hash[], BlockHeader header) {
        put(Utils.concatenate(BlockHeaderPrefix, hash), header);
    }

    public BlockHeader findHeader(byte[] hash) {
        byte header[] = get(Utils.concatenate(BlockHeaderPrefix, hash));
        if (header == null) {
            return null;
        }

        try {
            return new BlockHeader().fromBytes(header);
        } catch (WolkenException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void storeBlock(byte hash[], Block block) {
        storeHeader(hash, block.getBlockHeader());
        put(Utils.concatenate(BlockContentPrefix, hash), block.getPruned().getContent());
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
        put(concatenate(BlockIndexPrefix, Utils.takeApart(height)), block.getHash());
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
        byte hash[] = get(concatenate(BlockIndexPrefix, Utils.takeApart(height)));

        if (hash == null) {
            return null;
        }

        return findBlock(hash);
    }

    public void deleteBlock(int height) {
        byte key[]  = concatenate(BlockIndexPrefix, Utils.takeApart(height));
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

    public void put(byte[] k, SerializableI v) {
        mutex.lock();
        try {
            database.put(k, v.asByteArray());
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
        byte tip[] = get(ChainTipPrefix);

        if (tip != null) {
            return findBlock(Utils.trim(tip, 0, 32));
        }

        return null;
    }

    public byte[] findBlockHash(int height) {
        return get(concatenate(BlockIndexPrefix, Utils.takeApart(height)));
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

    public void storeAccount(byte[] address, Account account) {
        put(Utils.concatenate(AccountPrefix, address), account.asByteArray());
    }

    public void newAccount(byte[] address) {
        put(Utils.concatenate(AccountPrefix, address), new Account().asByteArray());
    }

    public void registerAlias(byte[] address, long alias) {
        put(Utils.concatenate(AliasPrefix, Utils.takeApartLong(alias)), address);
    }

    public void removeAccount(byte[] address) {
        Account account = findAccount(address);
        remove(Utils.concatenate(AccountPrefix, address));
        if (account != null && account.hasAlias()) {
            remove(Utils.concatenate(AliasPrefix, Utils.takeApartLong(account.getAlias())));
        }
    }

    public Account findAccount(long alias) {
        byte address[] = findAccountHolder(alias);
        if (address != null) {
            return findAccount(address);
        }

        return null;
    }

    public byte[] findAccountHolder(long alias) {
        return get(Utils.concatenate(AliasPrefix, Utils.takeApartLong(alias)));
    }

    public Account findAccount(byte address[]) {
        byte data[] = get(Utils.concatenate(AccountPrefix, address));

        if (data != null) {
            InputStream inputStream = new ByteArrayInputStream(data);
            Account account = new Account();
            try {
                account.read(inputStream);
                inputStream.close();
            } catch (IOException | WolkenException e) {
                e.printStackTrace();
                return null;
            }

            return account;
        }

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
        return get(concatenate(TransactionPrefix, txid)) != null;
    }

    public boolean checkAccountExists(long alias) {
        return false;
    }

    public boolean checkAccountExists(byte[] address) {
        return false;
    }

    public void markRejected(byte[] hash) {
        put(Utils.concatenate(RejectedBlockPrefix, hash), new byte[] { 1 });
    }

    public boolean isRejected(byte[] hash) {
        return get(Utils.concatenate(RejectedBlockPrefix, hash)) != null;
    }

    public Transaction findTransaction(byte[] txid) {
        return null;
    }

    public boolean checkWalletExists(String name) {
        return get(Utils.concatenate(WalletPrefix, name.getBytes())) != null;
    }

    public void storeWallet(Wallet wallet) {
        put(Utils.concatenate(WalletPrefix, wallet.getName().getBytes()), wallet.asByteArray());
    }

    public Wallet getWallet(String name) {
        return Wallet.fromBytes(name, get(Utils.concatenate(WalletPrefix, name.getBytes())));
    }
}
