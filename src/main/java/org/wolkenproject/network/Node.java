package org.wolkenproject.network;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    private Socket          socket;
    private ReentrantLock   mutex;
    private Queue<Message>  messages;
    private MessageCache    messageCache;
    private long            firstConnected;
    private int             errors;

    private BufferedInputStream     inputStream;
    private BufferedOutputStream    outputStream;

    private VersionInformation      versionMessage;

    public Node(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    public Node(Socket socket) throws IOException {
        this.socket         = socket;
        this.mutex          = new ReentrantLock();
        this.messages       = new ConcurrentLinkedQueue<>();
        this.messageCache   = new MessageCache();
        this.errors         = 0;
        this.inputStream    = new BufferedInputStream(socket.getInputStream(), Context.getInstance().getNetworkParameters().getBufferSize());
        this.outputStream   = new BufferedOutputStream(socket.getOutputStream(), Context.getInstance().getNetworkParameters().getBufferSize());
        this.firstConnected = System.currentTimeMillis();
    }

    /*
        Sends a message only if it was not sent before.
     */
    public void sendMessage(Message message) {
        mutex.lock();
        try{
            if (messageCache.shouldSend(message))
            {
                messages.add(message);
            }
        } finally {
            mutex.unlock();
        }
    }

    /*
        Forcefully sends a message even if it was sent before.
     */
    public void forceSendMessage(Message message) {
        mutex.lock();
        try{
            messages.add(message);
        } finally {
            mutex.unlock();
        }
    }

    /*
        Listens for any incoming messages.
     */
    public CachedMessage listenForMessage() {
        try {
            // a loop that hangs the entire thread might be dangerous.
            //         while ((read = stream.read(messageHeader, read, messageHeader.length - read)) != messageHeader.length);
            byte magicBytes[]    = new byte[4];
            inputStream.read(magicBytes);
            // this is unused as of this version
            // but it is needed.
            int magic       = Utils.makeInt(magicBytes);
            Message message = Context.getInstance().getSerialFactory().fromStream(magic, inputStream);
            return checkSpam(message);
        } catch (IOException | WolkenException e) {
            errors ++;
            return null;
        }
    }

    /*
        Caches the message and keeps track of how many times it was received.
     */
    private CachedMessage checkSpam(Message message) {
        return new CachedMessage(message, messageCache.cacheReceivedMessage(message) >= Context.getInstance().getNetworkParameters().getMessageSpamThreshold());
    }

    /*
        Send all queued messages.
     */
    public void flush()
    {
        mutex.lock();
        try{
            while (!messages.isEmpty()) {
                Message message = messages.poll();
                message.write(outputStream);
                outputStream.flush();
            }
        } catch (IOException | WolkenException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }

    public void close() throws IOException {
        outputStream.flush();
        socket.close();
        inputStream.close();
        outputStream.close();
    }

    public int getTotalErrorCount() {
        return errors;
    }

    public double getSpamAverage() {
        return messageCache.getAverageSpam();
    }

    public InetAddress getInetAddress() {
        return ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress();
    }

    public MessageCache getMessageCache()
    {
        return messageCache;
    }

    public void setVersionInfo(VersionInformation versionMessage) {
        this.versionMessage = versionMessage;
        getNetAddress().setServices(versionMessage.getServices());
    }

    public boolean hasPerformedHandshake()
    {
        return versionMessage != null;
    }

    public NetAddress getNetAddress() {
        NetAddress address = Context.getInstance().getIpAddressList().getAddress(getInetAddress());
        if (address == null)
        {
            long services = 0;
            if (versionMessage != null)
            {
                services = versionMessage.getServices();
            }

            address = new NetAddress(getInetAddress(), getPort(), services);
            Context.getInstance().getIpAddressList().addAddress(address);
        }

        return address;
    }

    public int getPort() {
        return socket.getPort();
    }

    public long timeSinceConnected()
    {
        return System.currentTimeMillis() - firstConnected;
    }
}
