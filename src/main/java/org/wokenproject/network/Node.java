package org.wokenproject.network;

import org.wokenproject.core.Context;
import org.wokenproject.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    private Socket          socket;
    private ReentrantLock   mutex;
    private Queue<Message>  messages;
    private MessageCache    messageCache;
    private int             errors;

    private BufferedInputStream     inputStream;
    private BufferedOutputStream    outputStream;

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
    public Message listenForMessage() {
        try {
            // a loop that hangs the entire thread might be dangerous.
            //         while ((read = stream.read(messageHeader, read, messageHeader.length - read)) != messageHeader.length);
            byte magicBytes[]    = new byte[4];
            // this is unused as of this version
            // but it is needed.
            int magic            = Utils.makeInt(magicBytes);

            byte messageHeader[] = new byte[20];

            int read = inputStream.read(messageHeader);
            if (read != messageHeader.length) {
                errors++;
                return null;
            }

            int version = Utils.makeInt(messageHeader);
            int flags   = Utils.makeInt(messageHeader, 4);
            int type    = Utils.makeInt(messageHeader, 8);
            int count   = Utils.makeInt(messageHeader, 12);
            int length  = Utils.makeInt(messageHeader, 16);

            byte content[] = new byte[length];

            read = inputStream.read(content); // while (read < content.length) { read += stream.read(content, read, content.length - read); }
            if (read != content.length) {
                errors++;
                return null;
            }

            return checkSpam(new Message(version, flags, type, count, content));
        } catch (IOException e) {
            errors ++;
            return null;
        }
    }

    /*
        Caches the message and keeps track of how many times it was received.
     */
    private Message checkSpam(Message message) {
        messageCache.cacheReceivedMessage(message);
        return message;
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
                message.writeToStream(outputStream);
            }
        } catch (IOException e) {
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
        return socket.getInetAddress();
    }

    public MessageCache getMessageCache()
    {
        return messageCache;
    }
}
