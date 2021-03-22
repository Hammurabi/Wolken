package org.wolkenproject.rpc;

import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;

public class Messenger {
    private HttpExchange    exchange;
    private String          url;
    private String          query;

    public Messenger(HttpExchange exchange, String url, String query) {
        this.exchange = exchange;
    }

    public void sendFile(InputStream inputStream) {
        sendFile("text/html", inputStream);
    }

    public void sendFile(String contentType, InputStream inputStream) {
        ;
    }
}
