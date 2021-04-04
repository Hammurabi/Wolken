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

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import static org.wolkenproject.utils.Utils.concatenate;

public class Database {
    private DB              database;
    private FileService     location;
    private FileService     blocks;
    private FileService     events;
    private ReentrantLock   mutex;

    private final static byte[]
    AccountPrefix           = new byte[] { 'a' },
    AliasPrefix             = new byte[] { 'A' },
    ChainTipPrefix          = new byte[] { 'q' },
    BlockPrefix             = new byte[] { 'b' },
    PrunedBlockPrefix       = new byte[] { 'p' },
    CompressedBlockPrefix   = new byte[] { 's' },
    BlockIndexPrefix        = new byte[] { 'i' },
    BlockFile               = new byte[] { 'f' },
    TransactionPrefix       = new byte[] { 't' },
    RejectedBlockPrefix     = new byte[] { 'r' },
    WalletPrefix            = new byte[] { 'w' };

    public Database(FileService location) throws IOException {
        this.blocks = location.newFile(".chain").newFile("blocks");
        this.events = location.newFile(".chain").newFile("events");

        if (!blocks.exists()) {
            blocks.makeDirectories();
        }

        if (!events.exists()) {
            blocks.makeDirectories();
        }

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

    public void storeTransaction(byte[] hash, Transaction transaction, int block) {
        // store the transaction metadata
        put(Utils.concatenate(TransactionPrefix, hash), Utils.concatenate(
                                                                Utils.takeApart(transaction.getVersion()),
                                                                Utils.takeApart(transaction.getTransactionValue()),
                                                                Utils.takeApart(transaction.getTransactionFee()),
                                                                Utils.takeApart(block)));
    }

    public Transaction findTransaction(byte[] hash) {
        byte bytes[] = get(Utils.concatenate(TransactionPrefix, hash));

        if (bytes == null) {
            return null;
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            return Context.getInstance().getSerialFactory().fromStream(inputStream);
        } catch (WolkenException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BlockIndex findBlock(byte[] hash) {s
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

    public void storePrunedBlock(int height, BlockIndex block) {
        // get a reference of the hash since we will keep reusing it.
        byte hash[]   = block.getHash();

        // 80 bytes representing the block header.
        byte header[] = block.getBlock().getHeaderBytes();

        // 28 bytes representing the height, number of transactions, number of events total value, and total fees.
        byte metadt[] = Utils.concatenate(
                Utils.takeApart(height),
                Utils.takeApart(block.getBlock().getTransactionCount()),
                Utils.takeApart(block.getBlock().getEventCount()),
                Utils.takeApartLong(block.getBlock().getTotalValue()),
                Utils.takeApartLong(block.getBlock().getFees())
        );

        try {
            // get the raw uncompressed block as byte array.
            byte pruned[] = block.getPruned().asByteArray();

            // store the info that block of height 'height' is block of hash 'hash'.
            put(concatenate(BlockIndexPrefix, Utils.takeApart(height)), hash);

            // store the header along with the height and number of transactions and number of events.
            put(concatenate(BlockPrefix, hash), Utils.concatenate(header, metadt));

            // store the actual compressed block data.
            put(concatenate(PrunedBlockPrefix, hash), pruned);
        } catch (WolkenException e) {
            e.printStackTrace();
        }
    }

    public void storeBlock(int height, BlockIndex block) {
        // get a reference of the hash since we will keep reusing it.
        byte hash[]   = block.getHash();

        // 80 bytes representing the block header.
        byte header[] = block.getBlock().getHeaderBytes();

        try {
            // 28 bytes representing the height, number of transactions, number of events total value, and total fees.
            BlockMetadata blockMeta = block.getMetadata();

            // prepare a byte array output stream for quickly serializing the block structure to a byte array.
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // we serialize the block into a deflater output stream with BEST_COMPRESSION, which is slow
            // but according to benchmarks, it should take around 7ms per block to deflate, and around 14ms
            // to inflate.
            DeflaterOutputStream outputStream = new DeflaterOutputStream(byteArrayOutputStream, new Deflater(Deflater.BEST_COMPRESSION));

            // write the block (LOCALLY) to the output stream.
            block.getBlock().write(outputStream, true);

            outputStream.flush();
            outputStream.close();

//            // store the actual transactions associated with this block
//            for (Transaction transaction : block.getBlock()) {
//                storeTransaction(transaction.getHash(), transaction, block.getHeight());
//            }

            // store the info that block of height 'height' is block of hash 'hash'.
            put(concatenate(BlockIndexPrefix, Utils.takeApart(height)), hash);

            // store the header along with the height and number of transactions and number of events.
            put(concatenate(BlockPrefix, hash), Utils.concatenate(header, metadt));

            // store the actual compressed block data.
            put(concatenate(CompressedBlockPrefix, hash), byteArrayOutputStream.toByteArray());
        } catch (WolkenException | IOException e) {
            e.printStackTrace();
        }
    }

    private BlockStore findBlockStore(int blockStore) {
        byte bytes[] = get(Utils.concatenate(BlockFile, Utils.takeApart(blockStore)));
        if (bytes == null) {
            return null;
        }

        return new BlockStore(bytes);
    }

    private boolean checkBlockFileExists(int blockFile) {
        return blocks.newFile("block_" + blockFile).exists();
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

        if (hash != null) {
            remove(key);
            remove(concatenate(BlockPrefix, hash));
            remove(concatenate(CompressedBlockPrefix, hash));
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

    public boolean checkWalletExists(String name) {
        return get(Utils.concatenate(WalletPrefix, name.getBytes())) != null;
    }

    public void storeWallet(Wallet wallet) {
        put(Utils.concatenate(WalletPrefix, wallet.getName().getBytes()), wallet.asByteArray());
    }

    public Wallet getWallet(String name) {
        return Wallet.fromBytes(name, get(Utils.concatenate(WalletPrefix, name.getBytes())));
    }

    public void registerAlias(byte[] address, long alias) {
        put(Utils.concatenate(AliasPrefix, Utils.takeApartLong(alias)), address);
    }

    public void removeAlias(long alias) {
        remove(Utils.concatenate(AliasPrefix, Utils.takeApartLong(alias)));
    }
}
