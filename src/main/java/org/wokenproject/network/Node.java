package org.wokenproject.network;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.utils.Utils;

import java.io.BufferedInputStream;
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

    private BufferedInputStream inputStream;

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
        byte messageHeader[] = new byte[20];

        int read = stream.read(messageHeader);
        if (read != messageHeader.length)
        {
            return null;
        }

        int version = Utils.makeInt(messageHeader);
        int flags   = Utils.makeInt(messageHeader);
        int type    = Utils.makeInt(messageHeader);
        int count   = Utils.makeInt(messageHeader);
        int length  = Utils.makeInt(messageHeader);

        byte content[] = new byte[length];

        read = stream.read(content); // while (read < content.length) { read += stream.read(content, read, content.length - read); }
        if (read != content.length)
        {
            return null;
        }

        return new Message(version, flags, type, count, content);
    }
}
