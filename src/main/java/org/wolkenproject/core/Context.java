package org.wolkenproject.core;

import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.*;
import org.wolkenproject.network.messages.*;
import org.wolkenproject.papaya.runtime.*;
import org.wolkenproject.rpc.RpcServer;
import org.wolkenproject.serialization.SerializationFactory;
import org.wolkenproject.utils.FileService;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashSet;
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
    private OpcodeRegister opcodeRegister;
    private ResourceManager         resourceManager;
    private RpcServer               rpcServer;
    private FileService             fileService;
    private CompressionEngine       compressionEngine;

    public Context(FileService service, int rpcPort, boolean testNet, Address[] payList, Set<NetAddress> forceConnections) throws WolkenException, IOException {
        Context.instance = this;
        this.database = new Database(service.newFile("db"));
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

        Transaction.register(serializationFactory);
        serializationFactory.registerClass(RecoverableSignature.class, new RecoverableSignature());

        serializationFactory.registerClass(BlockHeader.class, new BlockHeader());
        serializationFactory.registerClass(Block.class, new Block());
        serializationFactory.registerClass(BlockIndex.class, new BlockIndex());
        serializationFactory.registerClass(Ancestors.class, new Ancestors(new byte[Block.UniqueIdentifierLength]));

        serializationFactory.registerClass(NetAddress.class, new NetAddress(InetAddress.getLocalHost(), 0, 0));
        serializationFactory.registerClass(VersionMessage.class, new VersionMessage());
        serializationFactory.registerClass(VerackMessage.class, new VerackMessage());
        serializationFactory.registerClass(VersionInformation.class, new VersionInformation());
        serializationFactory.registerClass(CheckoutMessage.class, new CheckoutMessage(0));

        serializationFactory.registerClass(BlockList.class, new BlockList(0, new LinkedHashSet<>(), new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(FailedToRespondMessage.class, new FailedToRespondMessage(0, 0, new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(FoundCommonAncestor.class, new FoundCommonAncestor(new byte[Block.UniqueIdentifierLength], new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(HeaderList.class, new HeaderList(0, new LinkedHashSet<>(), new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(Inv.class, new Inv(0, 0, new LinkedHashSet<>()));
        serializationFactory.registerClass(RequestBlocks.class, new RequestBlocks(0, new LinkedHashSet<>()));
        serializationFactory.registerClass(RequestCommonAncestorChain.class, new RequestCommonAncestorChain(0, new Ancestors(new byte[Block.UniqueIdentifierLength])));
        serializationFactory.registerClass(RequestHeaders.class, new RequestHeaders(0, new LinkedHashSet<>()));
        serializationFactory.registerClass(RequestHeadersBefore.class, new RequestHeadersBefore(0, new byte[Block.UniqueIdentifierLength], 0, new BlockHeader()));
        serializationFactory.registerClass(RequestInv.class, new RequestInv(0));
        serializationFactory.registerClass(RequestTransactions.class, new RequestTransactions(0, new LinkedHashSet<>()));
        serializationFactory.registerClass(TransactionList.class, new TransactionList(0, new LinkedHashSet<>(), new byte[Message.UniqueIdentifierLength]));
        serializationFactory.registerClass(AddressList.class, new AddressList(0, new LinkedHashSet<>()));

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