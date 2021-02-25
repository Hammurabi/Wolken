package org.wokenproject.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    private Socket          socket;
    private ReentrantLock   mutex;
    private Queue<Message>  messages;

    public Node(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    public Node(Socket socket) {
        this.socket = socket;
        this.mutex  = new ReentrantLock();
        this.messages = new ConcurrentLinkedQueue<>();
    }

    void sendMessage(Message message) {
        messages.add(message);
    }
}
