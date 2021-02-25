package org.wokenproject.network;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.WolkenException;

import java.io.IOException;
import java.io.InputStream;
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

    private byte            messageHeader[];
    private byte            buffer[];

    public Node(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    public Node(Socket socket) {
        this.socket         = socket;
        this.mutex          = new ReentrantLock();
        this.messages       = new ConcurrentLinkedQueue<>();
        this.messageCache   = new MessageCache();
        this.errors         = 0;
        this.messageHeader  = new byte[16];
        this.buffer         = new byte[Context.getInstance().getNetworkParameters().getBufferSize()];
    }

    public void sendMessage(Message message) {
        if (messageCache.shouldSend(message))
        {
            messages.add(message);
        }
    }

    private Message listen() throws IOException {
        InputStream stream = socket.getInputStream();

        // a loop that hangs the entire thread might be dangerous.
        //         while ((read = stream.read(messageHeader, read, messageHeader.length - read)) != messageHeader.length);
        int read = stream.read(messageHeader);
        if (read != messageHeader.length)
        {
            return null;
        }
    }
}
