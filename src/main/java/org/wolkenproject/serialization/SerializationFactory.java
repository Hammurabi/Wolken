package org.wolkenproject.serialization;

import org.wolkenproject.core.*;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.exceptions.InvalidSerialNumberException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.AddressList;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.NetAddress;
import org.wolkenproject.network.VersionInformation;
import org.wolkenproject.network.messages.*;
import org.wolkenproject.utils.Logger;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class SerializationFactory {
    private Map<Class<?>, Integer>      classMagicReferences;
    private Map<Integer, SerializableI> magicReferences;

    public SerializationFactory()
    {
        classMagicReferences = new HashMap<>();
        magicReferences = new HashMap<>();
    }

    public static void register(SerializationFactory serializationFactory) throws UnknownHostException, WolkenException {
        Transaction.register(serializationFactory);
        serializationFactory.registerClass(RecoverableSignature.class, new RecoverableSignature());

        serializationFactory.registerClass(BlockHeader.class, new BlockHeader());
        serializationFactory.registerClass(Block.class, new Block());
        serializationFactory.registerClass(BlockIndex.class, new BlockIndex());
        serializationFactory.registerClass(Ancestors.class, new Ancestors(new byte[Block.UniqueIdentifierLength]));

        // messages
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

        serializationFactory.registerClass(Account.class, new Account());
    }

    /*
        register an instance of serializable object.
     */
    public void registerClass(Class<?> classType, SerializableI serializableInstance)
    {
        int magic = 1 + classMagicReferences.size();
        classMagicReferences.put(classType, magic);
        magicReferences.put(magic, serializableInstance);
    }

    public <Type extends SerializableI> Type fromStream(Class<?> classType, InputStream stream) throws IOException, WolkenException {
        return fromStream(getSerialNumber(classType), stream);
    }

    public <Type extends SerializableI> Type fromStream(InputStream stream) throws IOException, WolkenException {
        int magic = VarInt.readCompactUInt32(false, stream);

        return fromStream(magic, stream);
    }

    public <Type extends SerializableI> Type fromStream(int magic, InputStream stream) throws IOException, WolkenException {
        SerializableI serializable  = magicReferences.get(validateMagicNumber(magic));

        Type result                 = serializable.newInstance();
        result.read(stream);

        return result;
    }

    private int validateMagicNumber(int magic) throws InvalidSerialNumberException {
        if (magicReferences.containsKey(magic))
        {
            return magic;
        }

        throw new InvalidSerialNumberException("'" + magic + "' is an invalid serial number.");
    }

    public int getSerialNumber(Class<?> classType) {
        return classMagicReferences.get(classType);
    }
}
