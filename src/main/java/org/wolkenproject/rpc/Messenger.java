package org.wolkenproject.rpc;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;

public class Messenger {
    private HttpExchange    exchange;
    private String          url;
    private String          query;

    public Messenger(HttpExchange exchange) {
        this.exchange   = exchange;

        query           = exchange.getRequestURI().getQuery();

        if (query == null) {
            query = "";
        }

        url             = exchange.getRequestURI().toString().replace(query, "");
    }

    public void sendFile(InputStream inputStream) {
        sendFile("text/html", inputStream);
    }

    public void sendFile(String contentType, InputStream inputStream) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", contentType);
    }

    public String getQuery() {
        return query;
    }

    public String getUrl() {
        return url;
    }
}
