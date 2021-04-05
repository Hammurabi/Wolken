package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.*;
import org.wolkenproject.papaya.runtime.*;
import org.wolkenproject.rpc.RpcServer;
import org.wolkenproject.serialization.SerializationFactory;
import org.wolkenproject.utils.FileService;

import java.io.IOException;
import java.util.Set;
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
    private BlockChain              blockChain;
    private OpcodeRegister          opcodeRegister;
    private ResourceManager         resourceManager;
    private RpcServer               rpcServer;
    private FileService             fileService;
    private CompressionEngine       compressionEngine;

    public Context(FileService service, int rpcPort, boolean testNet, Address[] payList, Set<NetAddress> forceConnections) throws WolkenException, IOException {
        Context.instance = this;
        this.database = new Database(service);
        this.networkParameters = new NetworkParameters(testNet);
        this.threadPool = Executors.newFixedThreadPool(3);
        this.isRunning = new AtomicBoolean(true);
        this.ipAddressList = new IpAddressList(service.newFile("peers"));
        this.serializationFactory = new SerializationFactory();
        this.transactionPool = new TransactionPool();
        this.payList = payList;
        this.fileService = service;
        this.opcodeRegister = new OpcodeRegister();
        this.resourceManager = new ResourceManager();
        this.compressionEngine = new CompressionEngine();

        SerializationFactory.register(serializationFactory);
        OpcodeRegister.register(opcodeRegister);

        this.blockChain = new BlockChain(this);
        this.server = new Server(forceConnections);

        getThreadPool().execute(server);
        getThreadPool().execute(blockChain);

        this.rpcServer = new RpcServer(this, rpcPort);
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            if (Context.getInstance().isRunning()) {
                Context.getInstance().shutDown();
            }
        }));
    }

    public void shutDown() {
        isRunning.set(false);
        server.shutdown();
        rpcServer.stop();
        try {
            ipAddressList.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        threadPool.shutdownNow();
    }

    public Database getDatabase() {
        return database;
    }

    public NetworkParameters getNetworkParameters() {
        return networkParameters;
    }

    public static Context getInstance() {
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

    public BlockChain getBlockChain() {
        return blockChain;
    }

    public OpcodeRegister getOpcodeRegister() {
        return opcodeRegister;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public RpcServer getRPCServer() {
        return rpcServer;
    }

    public CompressionEngine getCompressionEngine() {
        return compressionEngine;
    }
}