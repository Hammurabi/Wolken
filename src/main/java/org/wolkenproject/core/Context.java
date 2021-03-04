package org.wolkenproject.core;

import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.*;
import org.wolkenproject.network.messages.*;
import org.wolkenproject.serialization.SerializationFactory;
import org.wolkenproject.utils.FileService;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Context {
    private static Context instance;

    private Database                database;
    private NetworkParameters       networkParameters;
    private ExecutorService         threadPool;
    private AtomicBoolean           isRunning;
    private IpAddressList           ipAddressList;
    private SerializationFactory    serializationFactory;
    private TransactionPool         transactionPool;
    private Server                  server;
    private Address                 payList[];
    private FileService             fileService;

    public Context(FileService service, boolean testNet, Address[] payList) throws WolkenException, IOException {
        Context.instance            = this;
        this.database               = new Database(service.newFile("db"));
        this.networkParameters      = new NetworkParameters(testNet);
        this.threadPool             = Executors.newFixedThreadPool(3);
        this.isRunning              = new AtomicBoolean(true);
        this.ipAddressList          = new IpAddressList(service.newFile("peers"));
        this.serializationFactory   = new SerializationFactory();
        this.transactionPool        = new TransactionPool();
        this.payList                = payList;
        this.fileService            = service;

        serializationFactory.registerClass(BlockHeader.class, new BlockHeader());
        serializationFactory.registerClass(Block.class, new Block());
        serializationFactory.registerClass(BlockIndex.class, new BlockIndex());

        serializationFactory.registerClass(Transaction.class, new Transaction(0, 0, 0, new Input[0], new Output[0]));
        serializationFactory.registerClass(Input.class, new Input(new byte[32], 0, new byte[1]));
        serializationFactory.registerClass(Output.class, new Output(0, new byte[1]));

        serializationFactory.registerClass(NetAddress.class, new NetAddress(InetAddress.getLocalHost(), 0, 0));
        serializationFactory.registerClass(VersionMessage.class, new VersionMessage());
        serializationFactory.registerClass(VersionInformation.class, new VersionInformation());
        serializationFactory.registerClass(RequestInv.class, new RequestInv(0));
        serializationFactory.registerClass(Inv.class, new Inv(0, 0, new LinkedHashSet<>()));
        serializationFactory.registerClass(TransactionList.class, new TransactionList(0, new LinkedHashSet<>(), new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(RequestTransactions.class, new RequestTransactions(0, new LinkedHashSet<>()));

        this.server                 = new Server();
    }

    public void shutDown()
    {
        isRunning.set(false);
        server.shutdown();
        try {
            ipAddressList.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Database getDatabase()
    {
        return database;
    }

    public NetworkParameters getNetworkParameters()
    {
        return networkParameters;
    }

    public static Context getInstance()
    {
        return instance;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public IpAddressList getIpAddressList() {
        return ipAddressList;
    }

    public SerializationFactory getSerialFactory() {
        return serializationFactory;
    }

    public TransactionPool getTransactionPool() {
        return transactionPool;
    }

    public Address[] getPayList() {
        return payList;
    }

    public Server getServer() {
        return server;
    }
}
