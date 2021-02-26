package org.wokenproject.core;

import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.network.IpAddressList;
import org.wokenproject.network.NetAddress;
import org.wokenproject.network.Server;
import org.wokenproject.network.VersionInformation;
import org.wokenproject.network.messages.RequestTransactions;
import org.wokenproject.network.messages.Inv;
import org.wokenproject.network.messages.TransactionList;
import org.wokenproject.network.messages.VersionMessage;
import org.wokenproject.serialization.SerializationFactory;
import org.wokenproject.utils.FileService;

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
    private IpAddressList ipAddressList;
    private SerializationFactory    serializationFactory;
    private TransactionPool         transactionPool;
    private Server                  server;
    private FileService             fileService;

    public Context(FileService service, boolean testNet) throws WolkenException, IOException {
        this.database               = new Database(service.newFile("db"));
        this.networkParameters      = new NetworkParameters(testNet);
        this.threadPool             = Executors.newFixedThreadPool(3);
        this.isRunning              = new AtomicBoolean(true);
        this.ipAddressList          = new IpAddressList(service.newFile("iplist"));
        this.serializationFactory   = new SerializationFactory();
        this.transactionPool        = new TransactionPool();
        this.fileService            = service;

        serializationFactory.registerClass(NetAddress.class, new NetAddress(InetAddress.getLocalHost(), 0));
        serializationFactory.registerClass(NetAddress.class, new VersionMessage());
        serializationFactory.registerClass(NetAddress.class, new VersionInformation());
        serializationFactory.registerClass(Inv.class, new Inv(0, new LinkedHashSet<>()));
        serializationFactory.registerClass(TransactionList.class, new TransactionList(0, new LinkedHashSet<>()));
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
}
