package org.wolkenproject.rpc;

import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;

public class Messenger {
    private HttpExchange exchange;
    
    public Messenger(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void sendFile(InputStream inputStream) {
        sendFile("text/html", inputStream);
    }

    public void sendFile(String contentType, InputStream inputStream) {
    }
}
