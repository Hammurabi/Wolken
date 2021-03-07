package org.wolkenproject.core;

import org.wolkenproject.core.script.BitFields;
import org.wolkenproject.core.script.MochaObject;
import org.wolkenproject.core.script.OpcodeRegister;
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
    private BlockChain              blockChain;
    private OpcodeRegister virtualMachine;
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
        this.virtualMachine         = null;

        serializationFactory.registerClass(BlockHeader.class, new BlockHeader());
        serializationFactory.registerClass(Block.class, new Block());
        serializationFactory.registerClass(BlockIndex.class, new BlockIndex());
        serializationFactory.registerClass(Ancestors.class, new Ancestors(new byte[Block.UniqueIdentifierLength]));
        serializationFactory.registerClass(Ancestors.class, new Input(new byte[TransactionI.UniqueIdentifierLength], 0, new byte[0]));
        serializationFactory.registerClass(Ancestors.class, new Output(0, new byte[0]));

        serializationFactory.registerClass(Transaction.class, new Transaction(0, 0, 0, new Input[0], new Output[0]));
        serializationFactory.registerClass(Input.class, new Input(new byte[32], 0, new byte[1]));
        serializationFactory.registerClass(Output.class, new Output(0, new byte[1]));

        serializationFactory.registerClass(NetAddress.class, new NetAddress(InetAddress.getLocalHost(), 0, 0));
        serializationFactory.registerClass(VersionMessage.class, new VersionMessage());
        serializationFactory.registerClass(VerackMessage.class, new VerackMessage());
        serializationFactory.registerClass(VersionInformation.class, new VersionInformation());

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

        serializationFactory.registerClass(MochaObject.class, new MochaObject());
        serializationFactory.registerClass(MochaObject.Metadata.class, new MochaObject());

        this.server                 = new Server();
        this.blockChain             = new BlockChain();

        virtualMachine.registerOp("halt", "halt process and all sub processes", null);
        virtualMachine.registerOp("arithmetic", new BitFields()
                                                    .addField(4, "operation")
                                                    .addField(4, "item0")
                                                    .addField(4, "item1")
                                                    .addField(4, "item2")
                                                    , "operations [+ - / * %] [& | ^ << >>] [< > ==] [4:op][4:item][4:item][4:result_item]", null);
        virtualMachine.registerOp("storei", new BitFields()
                                                    .addField(1, "sign")
                                                    .addField(3, "type")
                                                    .addField(4, "register")
                                                    , "pop x from stack and store it in register [1:sign][3:type][4:item]", null);
        virtualMachine.registerOp("loadi", new BitFields()
                                                    .addField(4, "item0")
                                                    .addField(1, "bool")
                                                    .addField(3, "item1")
                                                    , "load 1 or 2 items from registers and push them to stack [4:item0][1:bool][3:item1]", null);
        virtualMachine.registerOp("call", new BitFields()
                                                    .addField(4, "arg")
                                                    .addCond(4, (idx, prev, next, self)->{ return prev.getValue() == 0; }, "id")
                                                    .addCond(16, (idx, prev, next, self)->{ return prev.getValue() == 2; }, "id")
                                                    , "call a function [4:arg].", null);
        virtualMachine.registerOp("jump", new BitFields()
                                                    .addField(1, "get/set")
                                                    .addField(15, "id")
                                                    , "set or get (and jump) a jump location.", null);

//        virtualMachine.addOp("push", true, 0, 0, "push x amount of bytes into the stack", null);
//        virtualMachine.addOp("pop", false, 0, 0, "pop item from the stack", null);
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

    public BlockChain getBlockChain() {
        return blockChain;
    }
}
