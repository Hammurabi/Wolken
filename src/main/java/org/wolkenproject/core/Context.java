package org.wolkenproject.core;

import org.wolkenproject.core.script.*;
import org.wolkenproject.core.script.internal.MochaObject;
import org.wolkenproject.core.script.opcodes.OpHalt;
import org.wolkenproject.core.script.opcodes.OpIConst_4bits;
import org.wolkenproject.core.script.opcodes.OpPush;
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
    private OpcodeRegister          virtualMachine;
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

        virtualMachine = new OpcodeRegister();
        virtualMachine.registerOp("halt", "stop virtual process (and sub-processes).", 1, proc->{ proc.stopProcesses(Byte.toUnsignedInt(proc.getProgramCounter().get())); return null; });
        virtualMachine.registerOp("push", "push an array of bytes into the stack.", 1);

        virtualMachine.registerOp("const0", "push an integer with value '0' (unsigned).");
        virtualMachine.registerOp("const1", "push an integer with value '1' (unsigned).");
        virtualMachine.registerOp("const2", "push an integer with value '2' (unsigned).");
        virtualMachine.registerOp("const3", "push an integer with value '3' (unsigned).");
        virtualMachine.registerOp("const4", "push an integer with value '4' (unsigned).");
        virtualMachine.registerOp("const5", "push an integer with value '5' (unsigned).");
        virtualMachine.registerOp("const6", "push an integer with value '6' (unsigned).");
        virtualMachine.registerOp("const7", "push an integer with value '7' (unsigned).");
        virtualMachine.registerOp("const8", "push an integer with value '8' (unsigned).");
        virtualMachine.registerOp("const9", "push an integer with value '9' (unsigned).");
        virtualMachine.registerOp("const10", "push an integer with value '10' (unsigned).");
        virtualMachine.registerOp("const11", "push an integer with value '11' (unsigned).");
        virtualMachine.registerOp("const12", "push an integer with value '12' (unsigned).");
        virtualMachine.registerOp("const13", "push an integer with value '13' (unsigned).");
        virtualMachine.registerOp("const14", "push an integer with value '14' (unsigned).");
        virtualMachine.registerOp("const15", "push an integer with value '15' (unsigned).");

        virtualMachine.registerOp("bconst", "push an integer of size '8' (unsigned).", 1);
        virtualMachine.registerOp("iconst16", "push an integer of size '16' (unsigned).", 2);
        virtualMachine.registerOp("iconst32", "push an integer of size '32' (unsigned).", 4);
        virtualMachine.registerOp("iconst64", "push an integer of size '64' (unsigned).", 8);
        virtualMachine.registerOp("iconst128", "push an integer of size '128' integer (unsigned).", 16);
        virtualMachine.registerOp("iconst256", "push an integer of size '256' (unsigned).", 32);

        virtualMachine.registerOp("fconst", "push a float of size '32' (unsigned).", 4);
        virtualMachine.registerOp("fconst64", "push a float of size '64' (unsigned).", 8);
        virtualMachine.registerOp("fconst256", "push a float of size '256' (unsigned).", 32);

        virtualMachine.registerOp("flipsign", "pop an object from the stack and reinterpret the most significant bit as a sign bit.");

        virtualMachine.registerOp("add", "pop two objects from the stack and add them.");
        virtualMachine.registerOp("sub", "pop two objects from the stack and sub them.");
        virtualMachine.registerOp("mul", "pop two objects from the stack and mul them.");
        virtualMachine.registerOp("div", "pop two objects from the stack and div them.");
        virtualMachine.registerOp("mod", "pop two objects from the stack and mod them.");
        virtualMachine.registerOp("and", "pop two objects from the stack and perform bitwise and on them.");
        virtualMachine.registerOp("or", "pop two objects from the stack and perform bitwise or on them.");
        virtualMachine.registerOp("xor", "pop two objects from the stack and perform bitwise xor on them.");
        virtualMachine.registerOp("shf", "pop two objects from the stack and perform arithmetic shift on them.");
        virtualMachine.registerOp("lsh", "pop two objects from the stack left shift.");
        virtualMachine.registerOp("rsh", "pop two objects from the stack right shift.");
        virtualMachine.registerOp("not", "pop an object from the stack and perform bitwise not on it.");
        virtualMachine.registerOp("ngt", "pop an object from the stack and perform logical not on it.");

        virtualMachine.registerOp("dup1", "duplicate the first stack element (by reference).");
        virtualMachine.registerOp("dup2", "duplicate the second stack element (by reference).");
        virtualMachine.registerOp("dup3", "duplicate the third stack element (by reference).");
        virtualMachine.registerOp("dup4", "duplicate the fourth stack element (by reference).");
        virtualMachine.registerOp("dup5", "duplicate the fifth stack element (by reference).");
        virtualMachine.registerOp("dup6", "duplicate the sixth stack element (by reference).");
        virtualMachine.registerOp("dup7", "duplicate the seventh stack element (by reference).");
        virtualMachine.registerOp("dup8", "duplicate the eighth stack element (by reference).");
        virtualMachine.registerOp("dup9", "duplicate the ninth stack element (by reference).");
        virtualMachine.registerOp("dup10", "duplicate the tenth stack element (by reference).");
        virtualMachine.registerOp("dup11", "duplicate the eleventh stack element (by reference).");
        virtualMachine.registerOp("dup12", "duplicate the twelfth stack element (by reference).");
        virtualMachine.registerOp("dup13", "duplicate the thirteenth stack element (by reference).");
        virtualMachine.registerOp("dup14", "duplicate the fourteenth stack element (by reference).");
        virtualMachine.registerOp("dup15", "duplicate the fifteenth stack element (by reference).");
        virtualMachine.registerOp("dup16", "duplicate the sixteenth stack element (by reference).");

        virtualMachine.registerOp("swap1", "pop an object  the stack and perform logical not on it.");

        System.out.println(virtualMachine.opCount());
        System.exit(0);
//        serializationFactory.registerClass(MochaObject.class, new MochaObject());;

        MochaObject mochaObject = new MochaObject();

        this.server                 = new Server();
        this.blockChain             = new BlockChain();

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
