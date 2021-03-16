package org.wolkenproject.network;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.exceptions.WolkenTimeoutException;
import org.wolkenproject.network.messages.FailedToRespondMessage;
import org.wolkenproject.utils.Utils;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    private SocketChannel                   socket;
    private ReentrantLock                   mutex;
    private Queue<Message>                  messages;
    private Queue<byte[]>                   messageQueue;
    private Map<byte[], ResponseMetadata>   expectedResponse;
    private Map<byte[], Message>            respones;
    private MessageCache                    messageCache;
    private long                            firstConnected;
    private int                             errors;
    private ByteBuffer                      buffer;
    private ByteArrayOutputStream           stream;
    private int                             receivedAddresses;
    private VersionInformation              versionMessage;
    private boolean                         isClosed;

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
        this.expectedResponse = new HashMap<>();
    }

    public void receiveResponse(Message message, byte origin[]) {
        mutex.lock();
        try{
            respones.put(origin, message);
        } finally {
            mutex.unlock();
        }
    }

    public CheckedResponse getResponse(Message message, long timeOut) throws WolkenTimeoutException {
        boolean shouldWait = false;
        byte id[] = message.getUniqueMessageIdentifier();

        mutex.lock();
        try{
            if (messageCache.shouldSend(message))
            {
                messages.add(message);
                expectedResponse.put(id, message.getResponseMetadata());
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

    private CheckedResponse getResponse(byte[] uniqueMessageIdentifier) {
        mutex.lock();
        try{
            Message response            = respones.get(uniqueMessageIdentifier);
            ResponseMetadata metadata   = expectedResponse.get(uniqueMessageIdentifier);

            // internal error, we may return null
            if (response == null) {
                return null;
            }

            // may return without any issues
            if (response.getSerialNumber() == Context.getInstance().getSerialFactory().getSerialNumber(FailedToRespondMessage.class)) {
                return null;
            }

            int flags = metadata.getResponseBits(response);

            // check that the response is appropriate
            if ((flags & ResponseMetadata.ValidationBits.InvalidType) == ResponseMetadata.ValidationBits.InvalidType) {
                errors ++;
                return null;
            }

            // check that the response is appropriate
            if ((flags & ResponseMetadata.ValidationBits.SpamfulResponse) == ResponseMetadata.ValidationBits.SpamfulResponse) {
                errors += Context.getInstance().getNetworkParameters().getMaxNetworkErrors();
                messageCache.increaseSpamAverage(Context.getInstance().getNetworkParameters().getMessageSpamThreshold());
                return null;
            }

            return new CheckedResponse(response, flags);
        } finally {
            respones.remove(uniqueMessageIdentifier);
            expectedResponse.remove(uniqueMessageIdentifier);
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

    public void read() {
        mutex.lock();

        try {
            if (isClosed) {
                return;
            }

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
        } catch (IOException e) {
            e.printStackTrace();
            errors ++;
            if (stream != null) {
                stream = null;
            }
        } finally {
            mutex.unlock();
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

            byte msg[] = messageQueue.poll();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(msg);
            Message message = Context.getInstance().getSerialFactory().fromStream(inputStream);
            inputStream.close();
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
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Message message = messages.poll();
                message.write(outputStream);
                outputStream.flush();
                outputStream.close();

                byte msg[] = outputStream.toByteArray();
                int offset = 0;

                while (offset < msg.length) {
                    int remainder = msg.length - offset;
                    buffer.clear();
                    buffer.put(msg, offset, Math.min(Context.getInstance().getNetworkParameters().getBufferSize(), remainder));
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        offset += socket.write(buffer);
                    }
                }
            }
        } catch (IOException | WolkenException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }

    public void close() throws IOException {
        isClosed = true;
        socket.close();
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

    public void incrementReceivedAddresses() {
        receivedAddresses ++;

        if (receivedAddresses > 1024) {
            errors += Context.getInstance().getNetworkParameters().getMaxNetworkErrors();
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "socket=" + socket +
                ", mutex=" + mutex +
                ", messages=" + messages +
                ", messageQueue=" + messageQueue +
                ", expectedResponse=" + expectedResponse +
                ", respones=" + respones +
                ", messageCache=" + messageCache +
                ", firstConnected=" + firstConnected +
                ", errors=" + errors +
                ", buffer=" + buffer +
                ", stream=" + stream +
                ", receivedAddresses=" + receivedAddresses +
                ", versionMessage=" + versionMessage +
                ", isClosed=" + isClosed +
                '}';
    }
}
