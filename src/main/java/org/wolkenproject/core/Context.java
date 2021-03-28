package org.wolkenproject.core;

import org.wolkenproject.core.papaya.*;
import org.wolkenproject.core.papaya.internal.ByteArray;
import org.wolkenproject.core.papaya.internal.MochaNumber;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.*;
import org.wolkenproject.network.messages.*;
import org.wolkenproject.rpc.RpcServer;
import org.wolkenproject.serialization.SerializationFactory;
import org.wolkenproject.utils.FileService;

import java.io.IOException;
import java.math.BigInteger;
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
    private OpcodeRegister          opcodeRegister;
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

        final long WeightPush   = 2;
        final long WeightPop    = 2;

        opcodeRegister.registerOp("halt", "stop virtual process (and sub-processes).", 1, 1, scope -> scope.stopProcesses(scope.getProgramCounter().nextByte()));
        opcodeRegister.registerOp("pop", "pop the top element from the stack.", 1, scope -> scope.getStack().pop());
        opcodeRegister.registerOp("destroy", "destroy contract and return all funds to provided address.", 100, Scope::destroyContract);

        opcodeRegister.registerOp("call", "pop the top stack element and call it.", 2, 4, scope -> scope.getStack().push(scope.getStack().pop().call(scope)));

        opcodeRegister.registerOp("load", "load an object from an offset.", 3, 2, scope -> scope.getStack().pop().getMember(scope.getProgramCounter().nextUnsignedShort()));
        opcodeRegister.registerOp("store", "store an object to an offset.", 2, 2, scope -> scope.getStack().pop().setMember(scope.getProgramCounter().nextUnsignedShort(), scope.getStack().pop()));

        opcodeRegister.registerOp("getfield", "load an object from an offset in array.", 2, 2, scope -> scope.getStack().pop().subscriptSet((int) scope.getStack().pop().asInt(), scope.getStack().pop()));
        opcodeRegister.registerOp("setfield", "store an object to an offset in array.", 2, 2, scope -> scope.getStack().push(scope.getStack().pop().subscriptGet((int) scope.getStack().pop().asInt())));
        opcodeRegister.registerOp("append", "append an object to an array.", 2, scope -> scope.getStack().pop().append(scope.getStack().pop()));

        opcodeRegister.registerOp("pushdata", "push an array of bytes of length (8) into the stack.", true, 1, 1, scope -> scope.getStack().push(new ByteArray(scope.getProgramCounter().next(scope.getProgramCounter().nextByte()))));
        opcodeRegister.registerOp("pushdata2", "push an array of bytes of length (16) into the stack.", true, 2, 1, scope -> scope.getStack().push(new ByteArray(scope.getProgramCounter().next(scope.getProgramCounter().nextUnsignedShort()))));
        opcodeRegister.registerOp("pushdata3", "push an array of bytes of length (24) into the stack.", true, 3, 1, scope -> scope.getStack().push(new ByteArray(scope.getProgramCounter().next(scope.getProgramCounter().nextInt24()))));
        opcodeRegister.registerOp("push20", "push an array of bytes of length (160) into the stack.", true, 20, 1, scope -> scope.getStack().push(new ByteArray(scope.getProgramCounter().next(20))));

        opcodeRegister.registerOp("jmp", "jumps to a location in code", 1, scope -> scope.getProgramCounter().jump(scope.getProgramCounter().nextUnsignedShort()));
        opcodeRegister.registerOp("jnt", "branch operator, jumps if condition is not true.", 1, scope -> {
            if (!scope.getStack().pop().isTrue())
                scope.getProgramCounter().jump(scope.getProgramCounter().nextUnsignedShort());
        });

        opcodeRegister.registerOp("const0", "push an integer with value '0' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(0, false)));
        opcodeRegister.registerOp("const1", "push an integer with value '1' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(1, false)));
        opcodeRegister.registerOp("const2", "push an integer with value '2' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(2, false)));
        opcodeRegister.registerOp("const3", "push an integer with value '3' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(3, false)));
        opcodeRegister.registerOp("const4", "push an integer with value '4' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(4, false)));
        opcodeRegister.registerOp("const5", "push an integer with value '5' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(5, false)));
        opcodeRegister.registerOp("const6", "push an integer with value '6' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(6, false)));
        opcodeRegister.registerOp("const7", "push an integer with value '7' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(7, false)));
        opcodeRegister.registerOp("const8", "push an integer with value '8' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(8, false)));
        opcodeRegister.registerOp("const9", "push an integer with value '9' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(9, false)));
        opcodeRegister.registerOp("const10", "push an integer with value '10' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(10, false)));
        opcodeRegister.registerOp("const11", "push an integer with value '11' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(11, false)));
        opcodeRegister.registerOp("const12", "push an integer with value '12' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(12, false)));
        opcodeRegister.registerOp("const13", "push an integer with value '13' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(13, false)));
        opcodeRegister.registerOp("const14", "push an integer with value '14' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(14, false)));
        opcodeRegister.registerOp("const15", "push an integer with value '15' (unsigned).", 1, scope -> scope.getStack().push(new MochaNumber(15, false)));

        opcodeRegister.registerOp("bconst", "push an integer of size '8' (unsigned).", 1, 1, scope -> scope.getStack().push(new MochaNumber(scope.getProgramCounter().nextByte(), false)));
        opcodeRegister.registerOp("iconst16", "push an integer of size '16' (unsigned).", 2, 1, scope -> scope.getStack().push(new MochaNumber(scope.getProgramCounter().nextUnsignedShort(), false)));
        opcodeRegister.registerOp("iconst32", "push an integer of size '32' (unsigned).", 4, 1, scope -> scope.getStack().push(new MochaNumber(Integer.toUnsignedLong(scope.getProgramCounter().nextInt()), false)));
        opcodeRegister.registerOp("iconst64", "push an integer of size '64' (unsigned).", 8, 1, scope -> scope.getStack().push(new MochaNumber(Long.toUnsignedString(scope.getProgramCounter().nextLong()), false)));
        opcodeRegister.registerOp("iconst128", "push an integer of size '128' integer (unsigned).", 16, 1, scope -> scope.getStack().push(new MochaNumber(new BigInteger(1, scope.getProgramCounter().next(16)), false)));
        opcodeRegister.registerOp("iconst256", "push an integer of size '256' (unsigned).", 32, 1, scope -> scope.getStack().push(new MochaNumber(new BigInteger(1, scope.getProgramCounter().next(32)), false)));

        opcodeRegister.registerOp("fconst", "push a float of size '32' (unsigned).", 4, 1, scope -> {
            throw new MochaException("float is not supported at the moment.");
        });
        opcodeRegister.registerOp("fconst64", "push a float of size '64' (unsigned).", 8, 1, scope -> {
            throw new MochaException("float is not supported at the moment.");
        });
        opcodeRegister.registerOp("fconst256", "push a float of size '256' (unsigned).", 32, 1, scope -> {
            throw new MochaException("float is not supported at the moment.");
        });

        opcodeRegister.registerOp("aconst200", "push an address of size '200'.", 25, 1, scope -> {
            throw new MochaException("address is not supported at the moment.");
        });
        opcodeRegister.registerOp("aconst256", "push a hash of size '256'.", 32, 1, scope -> {
            throw new MochaException("hash256 is not supported at the moment.");
        });
//        opcodeRegister.registerOp("ecpub", "push a public key of size '264' (compressed).", 33, scope -> scope.getStack().push(new MochaPublicKey(new ECPublicKey(scope.getProgramCounter().next(33)))));
//        opcodeRegister.registerOp("ecsig", "push a signature of size '~'.", 73, scope -> scope.getStack().push(new MochaCryptoSignature(new RecoverableSignature((byte) scope.getProgramCounter().nextByte(), scope.getProgramCounter().next(32), scope.getProgramCounter().next(32)))));

        opcodeRegister.registerOp("verify", "throws an 'InvalidTransactionException' if the top stack item is not true.", 1, Scope::verify);
        opcodeRegister.registerOp("flipsign", "pop an object from the stack and reinterpret the most significant bit as a sign bit.", 1, scope -> scope.getStack().peek().flipSign());

        opcodeRegister.registerOp("add", "pop two objects from the stack and add them.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(0, "add").call(scope)));
        opcodeRegister.registerOp("sub", "pop two objects from the stack and sub them.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(1, "sub").call(scope)));
        opcodeRegister.registerOp("mul", "pop two objects from the stack and mul them.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(2, "mul").call(scope)));
        opcodeRegister.registerOp("div", "pop two objects from the stack and div them.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(3, "div").call(scope)));
        opcodeRegister.registerOp("mod", "pop two objects from the stack and mod them.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(4, "mod").call(scope)));
        opcodeRegister.registerOp("and", "pop two objects from the stack and perform bitwise and on them.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(5, "and").call(scope)));
        opcodeRegister.registerOp("or", "pop two objects from the stack and perform bitwise or on them.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(6, "or").call(scope)));
        opcodeRegister.registerOp("xor", "pop two objects from the stack and perform bitwise xor on them.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(7, "xor").call(scope)));
        opcodeRegister.registerOp("shf", "pop two objects from the stack and perform arithmetic shift on them.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(8, "shf").call(scope)));
        opcodeRegister.registerOp("rsh", "pop two objects from the stack right shift.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(9, "rsh").call(scope)));
        opcodeRegister.registerOp("lsh", "pop two objects from the stack left shift.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(10, "lsh").call(scope)));
        opcodeRegister.registerOp("not", "pop an object from the stack and perform bitwise not on it.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(11, "not").call(scope)));
        opcodeRegister.registerOp("ngt", "pop an object from the stack and perform logical not on it.", 1, scope -> scope.getStack().push(scope.getStack().peek().getMember(12, "ngt").call(scope)));

        opcodeRegister.registerOp("dup1", "duplicate the first stack element (by reference).", 1, scope -> scope.getStack().dup());
        opcodeRegister.registerOp("dup2", "duplicate the second stack element (by reference).", 1, scope -> scope.getStack().dup(2));
        opcodeRegister.registerOp("dup3", "duplicate the third stack element (by reference).", 1, scope -> scope.getStack().dup(3));
        opcodeRegister.registerOp("dup4", "duplicate the fourth stack element (by reference).", 1, scope -> scope.getStack().dup(4));
        opcodeRegister.registerOp("dup5", "duplicate the fifth stack element (by reference).", 1, scope -> scope.getStack().dup(5));
        opcodeRegister.registerOp("dup6", "duplicate the sixth stack element (by reference).", 1, scope -> scope.getStack().dup(6));
        opcodeRegister.registerOp("dup7", "duplicate the seventh stack element (by reference).", 1, scope -> scope.getStack().dup(7));
        opcodeRegister.registerOp("dup8", "duplicate the eighth stack element (by reference).", 1, scope -> scope.getStack().dup(8));
        opcodeRegister.registerOp("dup9", "duplicate the ninth stack element (by reference).", 1, scope -> scope.getStack().dup(9));
        opcodeRegister.registerOp("dup10", "duplicate the tenth stack element (by reference).", 1, scope -> scope.getStack().dup(10));
        opcodeRegister.registerOp("dup11", "duplicate the eleventh stack element (by reference).", 1, scope -> scope.getStack().dup(11));
        opcodeRegister.registerOp("dup12", "duplicate the twelfth stack element (by reference).", 1, scope -> scope.getStack().dup(12));
        opcodeRegister.registerOp("dup13", "duplicate the thirteenth stack element (by reference).", 1, scope -> scope.getStack().dup(13));
        opcodeRegister.registerOp("dup14", "duplicate the fourteenth stack element (by reference).", 1, scope -> scope.getStack().dup(14));
        opcodeRegister.registerOp("dup15", "duplicate the fifteenth stack element (by reference).", 1, scope -> scope.getStack().dup(15));
        opcodeRegister.registerOp("dup16", "duplicate the sixteenth stack element (by reference).", 1, scope -> scope.getStack().dup(16));

        opcodeRegister.registerOp("swap1", "swap two objects (the 1st and 2nd) on the stack.", 1, scope -> scope.getStack().swap(1, 2));
        opcodeRegister.registerOp("swap2", "swap two objects (the 1st and 3rd) on the stack.", 1, scope -> scope.getStack().swap(1, 3));
        opcodeRegister.registerOp("swap3", "swap two objects (the 1st and 4th) on the stack.", 1, scope -> scope.getStack().swap(1, 4));
        opcodeRegister.registerOp("swap4", "swap two objects (the 1st and 5th) on the stack.", 1, scope -> scope.getStack().swap(1, 5));
        opcodeRegister.registerOp("swap5", "swap two objects (the 1st and 6th) on the stack.", 1, scope -> scope.getStack().swap(1, 6));
        opcodeRegister.registerOp("swap6", "swap two objects (the 1st and 7th) on the stack.", 1, scope -> scope.getStack().swap(1, 7));
        opcodeRegister.registerOp("swap7", "swap two objects (the 1st and 8th) on the stack.", 1, scope -> scope.getStack().swap(1, 8));
        opcodeRegister.registerOp("swap8", "swap two objects (the 1st and 9th) on the stack.", 1, scope -> scope.getStack().swap(1, 9));
        opcodeRegister.registerOp("swap9", "swap two objects (the 1st and 10th) on the stack.", 1, scope -> scope.getStack().swap(1, 10));
        opcodeRegister.registerOp("swap10", "swap two objects (the 1st and 11th) on the stack.", 1, scope -> scope.getStack().swap(1, 11));
        opcodeRegister.registerOp("swap11", "swap two objects (the 1st and 12th) on the stack.", 1, scope -> scope.getStack().swap(1, 12));
        opcodeRegister.registerOp("swap12", "swap two objects (the 1st and 13th) on the stack.", 1, scope -> scope.getStack().swap(1, 13));
        opcodeRegister.registerOp("swap13", "swap two objects (the 1st and 14th) on the stack.", 1, scope -> scope.getStack().swap(1, 14));
        opcodeRegister.registerOp("swap14", "swap two objects (the 1st and 15th) on the stack.", 1, scope -> scope.getStack().swap(1, 15));
        opcodeRegister.registerOp("swap15", "swap two objects (the 1st and 16th) on the stack.", 1, scope -> scope.getStack().swap(1, 16));

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