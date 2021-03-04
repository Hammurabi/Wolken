package org.wolkenproject.network;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.exceptions.WolkenTimeoutException;
import org.wolkenproject.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Node implements Runnable {
    private SocketChannel           socket;
    private ReentrantLock           mutex;
    private Queue<Message>          messages;
    private Queue<byte[]>           messageQueue;
    private Map<byte[], Message>    respones;
    private MessageCache            messageCache;
    private long                    firstConnected;
    private int                     errors;
    private ByteBuffer              buffer;
    private ByteArrayOutputStream   stream;

//    private BufferedInputStream     inputStream;
//    private BufferedOutputStream    outputStream;

    private VersionInformation      versionMessage;

//    public Node(String ip, int port) throws IOException {
//        this(new Socket(ip, port));
//    }

    public Node(SocketChannel socket) throws IOException {
        this.socket         = socket;
        this.mutex          = new ReentrantLock();
        this.messageQueue   = new ConcurrentLinkedQueue<>();
        this.messages       = new ConcurrentLinkedQueue<>();
        this.messageCache   = new MessageCache();
        this.errors         = 0;
//        this.inputStream    = new BufferedInputStream(socket.getInputStream(), Context.getInstance().getNetworkParameters().getBufferSize());
//        this.outputStream   = new BufferedOutputStream(socket.getOutputStream(), Context.getInstance().getNetworkParameters().getBufferSize());
        this.firstConnected = System.currentTimeMillis();
        this.respones       = Collections.synchronizedMap(new HashMap<>());
        this.socket.configureBlocking(false);
        this.buffer         = ByteBuffer.allocate(Context.getInstance().getNetworkParameters().getBufferSize());
    }

    public void receiveResponse(Message message, byte origin[]) {
        mutex.lock();
        try{
            respones.put(origin, message);
        } finally {
            mutex.unlock();
        }
    }

    public Message getResponse(Message message, long timeOut) throws WolkenTimeoutException {
        boolean shouldWait = false;
        byte id[] = message.getUniqueMessageIdentifier();

        mutex.lock();
        try{
            if (messageCache.shouldSend(message))
            {
                messages.add(message);
                shouldWait = true;
            }
        } finally {
            mutex.unlock();
        }

        if (shouldWait) {
            long lastCheck = System.currentTimeMillis();

            while (System.currentTimeMillis() - lastCheck < timeOut) {
                if (hasResponse(id)) {
                    return getResponse(id);
                }
            }

            throw new WolkenTimeoutException("timed out while waiting for response");
        }

        return null;
    }

    private boolean hasResponse(byte[] uniqueMessageIdentifier) {
        mutex.lock();
        try{
            return respones.containsKey(uniqueMessageIdentifier);
        } finally {
            mutex.unlock();
        }
    }

    private Message getResponse(byte[] uniqueMessageIdentifier) {
        mutex.lock();
        try{
            return respones.get(uniqueMessageIdentifier);
        } finally {
            respones.remove(uniqueMessageIdentifier);
            mutex.unlock();
        }
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

    public void read() throws IOException {
        if (!socket.finishConnect()) {
            return;
        }

        if (stream == null) {
            stream = new ByteArrayOutputStream();
        }

        byte data[] = new byte[Context.getInstance().getNetworkParameters().getBufferSize()];

        int read = socket.read(buffer);
        long timestamp = System.currentTimeMillis();

        if (read == -1) {
            stream.flush();
            stream.close();

            // queue the message for processing.
            finish(stream);
        } else {
            // check message header
            if (stream.size() >= 12) {
                byte header[] = stream.toByteArray();
                int length = Utils.makeInt(header, 8);

                if (length > Context.getInstance().getNetworkParameters().getMaxMessageContentSize()) {
                    errors += Context.getInstance().getNetworkParameters().getMaxNetworkErrors();
                    stream = null;
                    close();
                    return;
                }
            }

            buffer.get(data, 0, read);
            stream.write(data, 0, read);
            buffer.clear();
        }
    }

    private void finish(ByteArrayOutputStream stream) {
        if (stream.size() > 0) {
            messageQueue.add(stream.toByteArray());
        }
    }

    /*
        Listens for any incoming messages.
     */
    public CachedMessage listenForMessage() {
        mutex.lock();
        try {
            if (messageQueue.isEmpty()) {
                return null;
            }
            
            int magic = Utils.makeInt(magicBytes);
            Message message = Context.getInstance().getSerialFactory().fromStream(magic, inputStream);
            return checkSpam(message);
        } catch (IOException | WolkenException e) {
            errors++;
            return null;
        } finally {
            mutex.unlock();
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
        try {
            return ((InetSocketAddress) socket.getRemoteAddress()).getAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
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
        try {
            return  ((InetSocketAddress) socket.getRemoteAddress()).getPort();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public long timeSinceConnected()
    {
        return System.currentTimeMillis() - firstConnected;
    }

    @Override
    public void run() {
        while (Context.getInstance().isRunning()) {
        }
    }
}
