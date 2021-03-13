package org.wolkenproject.core;

import org.wolkenproject.core.script.*;
import org.wolkenproject.core.script.internal.MochaNumber;
import org.wolkenproject.core.script.internal.MochaObject;
import org.wolkenproject.core.script.opcodes.OpHalt;
import org.wolkenproject.core.script.opcodes.OpIConst_4bits;
import org.wolkenproject.core.script.opcodes.OpPush;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.MochaException;
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
        virtualMachine.registerOp("halt", "stop virtual process (and sub-processes).", 1, proc->proc.stopProcesses(Byte.toUnsignedInt(proc.getProgramCounter().get())));
        virtualMachine.registerOp("push", "push an array of bytes into the stack.", 1, proc->{});

        virtualMachine.registerOp("const0", "push an integer with value '0' (unsigned).", proc->proc.getStack().push(new MochaNumber(0, false)));
        virtualMachine.registerOp("const1", "push an integer with value '1' (unsigned).", proc->proc.getStack().push(new MochaNumber(1, false)));
        virtualMachine.registerOp("const2", "push an integer with value '2' (unsigned).", proc->proc.getStack().push(new MochaNumber(2, false)));
        virtualMachine.registerOp("const3", "push an integer with value '3' (unsigned).", proc->proc.getStack().push(new MochaNumber(3, false)));
        virtualMachine.registerOp("const4", "push an integer with value '4' (unsigned).", proc->proc.getStack().push(new MochaNumber(4, false)));
        virtualMachine.registerOp("const5", "push an integer with value '5' (unsigned).", proc->proc.getStack().push(new MochaNumber(5, false)));
        virtualMachine.registerOp("const6", "push an integer with value '6' (unsigned).", proc->proc.getStack().push(new MochaNumber(6, false)));
        virtualMachine.registerOp("const7", "push an integer with value '7' (unsigned).", proc->proc.getStack().push(new MochaNumber(7, false)));
        virtualMachine.registerOp("const8", "push an integer with value '8' (unsigned).", proc->proc.getStack().push(new MochaNumber(8, false)));
        virtualMachine.registerOp("const9", "push an integer with value '9' (unsigned).", proc->proc.getStack().push(new MochaNumber(9, false)));
        virtualMachine.registerOp("const10", "push an integer with value '10' (unsigned).", proc->proc.getStack().push(new MochaNumber(10, false)));
        virtualMachine.registerOp("const11", "push an integer with value '11' (unsigned).", proc->proc.getStack().push(new MochaNumber(11, false)));
        virtualMachine.registerOp("const12", "push an integer with value '12' (unsigned).", proc->proc.getStack().push(new MochaNumber(12, false)));
        virtualMachine.registerOp("const13", "push an integer with value '13' (unsigned).", proc->proc.getStack().push(new MochaNumber(13, false)));
        virtualMachine.registerOp("const14", "push an integer with value '14' (unsigned).", proc->proc.getStack().push(new MochaNumber(14, false)));
        virtualMachine.registerOp("const15", "push an integer with value '15' (unsigned).", proc->proc.getStack().push(new MochaNumber(15, false)));

        virtualMachine.registerOp("bconst", "push an integer of size '8' (unsigned).", 1, proc->proc.getStack().push(new MochaNumber(proc.getProgramCounter().get(), false)));
        virtualMachine.registerOp("iconst16", "push an integer of size '16' (unsigned).", 2, proc->proc.getStack().push(new MochaNumber(proc.getProgramCounter().getChar(), false)));
        virtualMachine.registerOp("iconst32", "push an integer of size '32' (unsigned).", 4, proc->proc.getStack().push(new MochaNumber(Integer.toUnsignedLong(proc.getProgramCounter().getInt()), false)));
        virtualMachine.registerOp("iconst64", "push an integer of size '64' (unsigned).", 8, proc->proc.getStack().push(new MochaNumber(Long.toUnsignedString(proc.getProgramCounter().getLong()), false)));
        virtualMachine.registerOp("iconst128", "push an integer of size '128' integer (unsigned).", 16, proc->{ byte array[] = new byte[16]; proc.getProgramCounter().get(array); proc.getStack().push(new MochaNumber(0, false)); });
        virtualMachine.registerOp("iconst256", "push an integer of size '256' (unsigned).", 32, proc->proc.getStack().push(new MochaNumber(proc.getProgramCounter().get(), false)));

        virtualMachine.registerOp("fconst", "push a float of size '32' (unsigned).", 4, scope -> { throw new MochaException("float is not supported at the moment."); });
        virtualMachine.registerOp("fconst64", "push a float of size '64' (unsigned).", 8, scope -> { throw new MochaException("float is not supported at the moment."); });
        virtualMachine.registerOp("fconst256", "push a float of size '256' (unsigned).", 32, scope -> { throw new MochaException("float is not supported at the moment."); });

        virtualMachine.registerOp("flipsign", "pop an object from the stack and reinterpret the most significant bit as a sign bit.", scope -> scope.getStack().peek().flipSign());

        virtualMachine.registerOp("add", "pop two objects from the stack and add them.", scope -> scope.getStack().push(scope.getStack().peek().getMember(0, "add").call(scope)));
        virtualMachine.registerOp("sub", "pop two objects from the stack and sub them.", scope -> scope.getStack().push(scope.getStack().peek().getMember(1, "sub").call(scope)));
        virtualMachine.registerOp("mul", "pop two objects from the stack and mul them.", scope -> scope.getStack().push(scope.getStack().peek().getMember(2, "mul").call(scope)));
        virtualMachine.registerOp("div", "pop two objects from the stack and div them.", scope -> scope.getStack().push(scope.getStack().peek().getMember(3, "div").call(scope)));
        virtualMachine.registerOp("mod", "pop two objects from the stack and mod them.", scope -> scope.getStack().push(scope.getStack().peek().getMember(4, "mod").call(scope)));
        virtualMachine.registerOp("and", "pop two objects from the stack and perform bitwise and on them.", scope -> scope.getStack().push(scope.getStack().peek().getMember(5, "and").call(scope)));
        virtualMachine.registerOp("or", "pop two objects from the stack and perform bitwise or on them.", scope -> scope.getStack().push(scope.getStack().peek().getMember(6, "or").call(scope)));
        virtualMachine.registerOp("xor", "pop two objects from the stack and perform bitwise xor on them.", scope -> scope.getStack().push(scope.getStack().peek().getMember(7, "xor").call(scope)));
        virtualMachine.registerOp("shf", "pop two objects from the stack and perform arithmetic shift on them.", scope -> scope.getStack().push(scope.getStack().peek().getMember(8, "shf").call(scope)));
        virtualMachine.registerOp("lsh", "pop two objects from the stack left shift.", scope -> scope.getStack().push(scope.getStack().peek().getMember(9, "lsh").call(scope)));
        virtualMachine.registerOp("rsh", "pop two objects from the stack right shift.", scope -> scope.getStack().push(scope.getStack().peek().getMember(10, "rsh").call(scope)));
        virtualMachine.registerOp("not", "pop an object from the stack and perform bitwise not on it.", scope -> scope.getStack().push(scope.getStack().peek().getMember(11, "not").call(scope)));
        virtualMachine.registerOp("ngt", "pop an object from the stack and perform logical not on it.", scope -> scope.getStack().push(scope.getStack().peek().getMember(12, "ngt").call(scope)));

        virtualMachine.registerOp("dup1", "duplicate the first stack element (by reference).", scope -> scope.getStack().dup());
        virtualMachine.registerOp("dup2", "duplicate the second stack element (by reference).", scope -> scope.getStack().dup(2));
        virtualMachine.registerOp("dup3", "duplicate the third stack element (by reference).", scope -> scope.getStack().dup(3));
        virtualMachine.registerOp("dup4", "duplicate the fourth stack element (by reference).", scope -> scope.getStack().dup(4));
        virtualMachine.registerOp("dup5", "duplicate the fifth stack element (by reference).", scope -> scope.getStack().dup(5));
        virtualMachine.registerOp("dup6", "duplicate the sixth stack element (by reference).", scope -> scope.getStack().dup(6));
        virtualMachine.registerOp("dup7", "duplicate the seventh stack element (by reference).", scope -> scope.getStack().dup(7));
        virtualMachine.registerOp("dup8", "duplicate the eighth stack element (by reference).", scope -> scope.getStack().dup(8));
        virtualMachine.registerOp("dup9", "duplicate the ninth stack element (by reference).", scope -> scope.getStack().dup(9));
        virtualMachine.registerOp("dup10", "duplicate the tenth stack element (by reference).", scope -> scope.getStack().dup(10));
        virtualMachine.registerOp("dup11", "duplicate the eleventh stack element (by reference).", scope -> scope.getStack().dup(11));
        virtualMachine.registerOp("dup12", "duplicate the twelfth stack element (by reference).", scope -> scope.getStack().dup(12));
        virtualMachine.registerOp("dup13", "duplicate the thirteenth stack element (by reference).", scope -> scope.getStack().dup(13));
        virtualMachine.registerOp("dup14", "duplicate the fourteenth stack element (by reference).", scope -> scope.getStack().dup(14));
        virtualMachine.registerOp("dup15", "duplicate the fifteenth stack element (by reference).", scope -> scope.getStack().dup(15));
        virtualMachine.registerOp("dup16", "duplicate the sixteenth stack element (by reference).", scope -> scope.getStack().dup(16));

        virtualMachine.registerOp("swap1", "swap two objects (the 1st and 2nd) on the stack.", scope -> scope.getStack().swap(1, 2));
        virtualMachine.registerOp("swap2", "swap two objects (the 1st and 3rd) on the stack.", scope -> scope.getStack().swap(1, 3));
        virtualMachine.registerOp("swap3", "swap two objects (the 1st and 4th) on the stack.", scope -> scope.getStack().swap(1, 4));
        virtualMachine.registerOp("swap4", "swap two objects (the 1st and 5th) on the stack.", scope -> scope.getStack().swap(1, 5));
        virtualMachine.registerOp("swap5", "swap two objects (the 1st and 6th) on the stack.", scope -> scope.getStack().swap(1, 6));
        virtualMachine.registerOp("swap6", "swap two objects (the 1st and 7th) on the stack.", scope -> scope.getStack().swap(1, 7));
        virtualMachine.registerOp("swap7", "swap two objects (the 1st and 8th) on the stack.", scope -> scope.getStack().swap(1, 8));
        virtualMachine.registerOp("swap8", "swap two objects (the 1st and 9th) on the stack.", scope -> scope.getStack().swap(1, 9));
        virtualMachine.registerOp("swap9", "swap two objects (the 1st and 10th) on the stack.", scope -> scope.getStack().swap(1, 10));
        virtualMachine.registerOp("swap10", "swap two objects (the 1st and 11th) on the stack.", scope -> scope.getStack().swap(1, 11));
        virtualMachine.registerOp("swap11", "swap two objects (the 1st and 12th) on the stack.", scope -> scope.getStack().swap(1, 12));
        virtualMachine.registerOp("swap12", "swap two objects (the 1st and 13th) on the stack.", scope -> scope.getStack().swap(1, 13));
        virtualMachine.registerOp("swap13", "swap two objects (the 1st and 14th) on the stack.", scope -> scope.getStack().swap(1, 14));
        virtualMachine.registerOp("swap14", "swap two objects (the 1st and 15th) on the stack.", scope -> scope.getStack().swap(1, 15));
        virtualMachine.registerOp("swap15", "swap two objects (the 1st and 16th) on the stack.", scope -> scope.getStack().swap(1, 16));

        virtualMachine.registerOp("jnt", "branch operator, jumps if condition is not true.", scope -> { if (scope.getStack().pop().isTrue()) scope.getProgramCounter().position(scope.getProgramCounter().getChar()); });

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
